package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A "Marker" interface extending {@link Message}. When sending a Broadcast message
 * using the {@link MessageBus}, it will be received by all the subscribers of this
 * Broadcast-message type (the message Class).
 */
public interface Broadcast extends Message {

}
