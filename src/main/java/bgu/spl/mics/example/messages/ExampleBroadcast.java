package bgu.spl.mics.example.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

import java.util.concurrent.ConcurrentHashMap;

public class ExampleBroadcast implements Broadcast {

    private String senderId;

    public ExampleBroadcast(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

}
