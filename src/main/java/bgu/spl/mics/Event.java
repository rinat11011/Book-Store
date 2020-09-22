package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A "Marker" interface extending {@link Message}. A micro-service that sends an
 * Event message expects to receive a result of type {@code <T>} when a
 * micro-service that received the request has completed handling it.
 * When sending an event, it will be received only by a single subscriber in a
 * Round-Robin fashion.
 */
public interface Event<T> extends Message {
}
