package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
    private Customer currCustomer;
    private ConcurrentHashMap<Integer,LinkedBlockingQueue<BookInventoryInfo>> tickToBook = new ConcurrentHashMap<>();
    private CountDownLatch countDownLatch;

    public APIService(String name, CountDownLatch countDownLatch) {
        super(name);
        this.countDownLatch = countDownLatch;
    }

    public ConcurrentHashMap<Integer, LinkedBlockingQueue<BookInventoryInfo>> getTickToBook() {
        return tickToBook;
    }

    public void setCurrCustomer(Customer currCustomer) {
        this.currCustomer = currCustomer;
    }


    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class,call -> {
            if (call.getCurrentTick() == call.getDurationTick()){
                terminate();
            }

            else if(tickToBook.get(call.getCurrentTick()) != null){
                try {
                    while (!tickToBook.get(call.getCurrentTick()).isEmpty()) {
                        BookInventoryInfo book = tickToBook.get(call.getCurrentTick()).take();
                        sendEvent(new BookOrderEvent(currCustomer,book,call.getCurrentTick()));
                    }
                }
                catch (InterruptedException ex){
                    ex.printStackTrace();
                }


            }

        });
        countDownLatch.countDown();
    }

}
