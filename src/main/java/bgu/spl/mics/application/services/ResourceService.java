package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService {
    private ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
    private ConcurrentLinkedQueue<Future<DeliveryVehicle>> vehicleWaitingLine = new ConcurrentLinkedQueue<>();
    private CountDownLatch countDownLatch;


    public ResourceService(String name, CountDownLatch countDownLatch) {
        super(name);
        this.countDownLatch = countDownLatch;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, call -> {
            if (call.getCurrentTick() == call.getDurationTick()) {
                for (Future<DeliveryVehicle> future : vehicleWaitingLine) {
                    if (!future.isDone())
                        future.resolve(null);
                }
                terminate();
            }
        });

        subscribeEvent(AcquireVehicleEvent.class, call -> {
            Future<DeliveryVehicle> futureVehicle = resourcesHolder.acquireVehicle();
            if (!futureVehicle.isDone())
                vehicleWaitingLine.add(futureVehicle);
            complete(call, futureVehicle);
        });

        subscribeEvent(ReleaseVehicle.class, call -> {
            resourcesHolder.releaseVehicle(call.getVehicle());
            complete(call, null);
        });
        countDownLatch.countDown();
    }

}