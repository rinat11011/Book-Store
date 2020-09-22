package bgu.spl.mics;


import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    //--------Fields--------//

    private static MessageBusImpl instance = null;
    private ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<MicroService>> eventSubscribers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<MicroService>> broadcastSubscribers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<MicroService, BlockingQueue<Message>> messageBus = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Event, Future> eventToFuture = new ConcurrentHashMap<>();

    //--------Methods--------//

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        subscribeMessage(eventSubscribers, type, m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        subscribeMessage(broadcastSubscribers, type, m);
    }

    private void subscribeMessage(ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<MicroService>> hashmap, Class<? extends Message> type, MicroService m) {
        hashmap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
        synchronized (hashmap.get(type)) {
            if (!hashmap.get(type).contains(m))
                hashmap.get(type).add(m);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void complete(Event<T> e, T result) {////check!!!
        eventToFuture.get(e).resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if (broadcastSubscribers.get(b.getClass()) != null) {
            for (MicroService m : broadcastSubscribers.get(b.getClass())) {
                messageBus.get(m).add(b);
            }
        }
    }


    @Override
    public synchronized <T> Future<T> sendEvent(Event<T> e) {
        Future<T> futureEvent = new Future<>();
        eventToFuture.put(e, futureEvent);

        MicroService microService;
        if ((eventSubscribers.get(e.getClass()) == null) || (eventSubscribers.get(e.getClass()).isEmpty())) {
            return null;
        }
        microService = eventSubscribers.get(e.getClass()).poll();
        eventSubscribers.get(e.getClass()).add(microService);

        messageBus.get(microService).add(e);

        return futureEvent;
    }

    @Override
    public void register(MicroService m) {
        messageBus.put(m, new LinkedBlockingQueue<>());
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void unregister(MicroService m) {

        eventSubscribers.values().forEach(queue -> queue.remove(m));
        broadcastSubscribers.values().forEach(queue -> queue.remove(m));
        BlockingQueue<Message> q = messageBus.remove(m);
        for (Message message : q) {
            if(message instanceof Event)
                if(!eventToFuture.get(message).isDone())
                    eventToFuture.get(message).resolve(null);
            q.remove(message);
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (messageBus.get(m) == null) {
            throw new IllegalStateException("");
        }
        Message msg = messageBus.get(m).take();
        return msg;
    }

    public static MessageBusImpl getInstance() {
        if (instance == null) {
            instance = new MessageBusImpl();
        }
        return instance;
    }
}
