package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.concurrent.ConcurrentHashMap;

public class DeliveryEvent implements Event<Object> {

    //--------Fields--------//

    private String address;
    private int distance;

    //--------Constructor--------//

    public DeliveryEvent(String address,int distance){
        this.address = address;
        this.distance = distance;
    }

    //--------Methods--------//

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
