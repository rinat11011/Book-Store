package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicle implements Event<Object> {
    private DeliveryVehicle vehicle;

    public ReleaseVehicle(DeliveryVehicle vehicle){
        this.vehicle = vehicle;

    }

    public DeliveryVehicle getVehicle() {
        return vehicle;
    }

}
