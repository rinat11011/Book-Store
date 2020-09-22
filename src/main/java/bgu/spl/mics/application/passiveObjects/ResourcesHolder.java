package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

    //--------Fields--------//

    private static ResourcesHolder holder = null;
    private LinkedBlockingQueue<DeliveryVehicle> vehicles = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Future<DeliveryVehicle>> vehicleWaitingLine = new LinkedBlockingQueue<>();

    //--------Constructor--------//

    /**
     * Retrieves the single instance of this class.
     */
    public static ResourcesHolder getInstance() {
        if (holder == null) {
            holder = new ResourcesHolder();
        }
        return holder;
    }

    //--------Methods--------//

    /**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     *
     * @return {@link Future<DeliveryVehicle>} object which will resolve to a
     * {@link DeliveryVehicle} when completed.
     */
    public synchronized Future<DeliveryVehicle> acquireVehicle() {
        Future<DeliveryVehicle> future = new Future<>();
        if (!vehicles.isEmpty())
            future.resolve(vehicles.remove());
        else {
            vehicleWaitingLine.add(future);
        }
        return future;
    }

    /**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     *
     * @param vehicle {@link DeliveryVehicle} to be released.
     */
    public synchronized void releaseVehicle(DeliveryVehicle vehicle) {
        if (!vehicleWaitingLine.isEmpty()) {
            vehicleWaitingLine.remove().resolve(vehicle);
        } else {
            vehicles.add(vehicle);
        }
    }

    /**
     * Receives a collection of vehicles and stores them.
     * <p>
     *
     * @param vehicles Array of {@link DeliveryVehicle} instances to store.
     */
    public void load(DeliveryVehicle[] vehicles) {
        this.vehicles.addAll(Arrays.asList(vehicles));

    }
}
