package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.concurrent.ConcurrentHashMap;

public class TickBroadcast implements Broadcast{

    //--------Fields--------//

    private int currentTick;
    private int durationTick;

    //--------Constructor--------//

    public TickBroadcast( int currentTick,int duration){
        this.currentTick = currentTick;
        this.durationTick = duration;
    }

    //--------Methods--------//

    public int getDurationTick() {
        return durationTick;
    }
    public void setTick(int newTick){
        currentTick = newTick;
    }
    public int getCurrentTick(){
        return currentTick;
    }

}
