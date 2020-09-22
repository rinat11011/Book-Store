package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailablilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory = Inventory.getInstance();
	private CountDownLatch countDownLatch;


	 public InventoryService(String name,CountDownLatch countDownLatch) {
		super(name);
		this.countDownLatch = countDownLatch;

	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, call-> {
			if(call.getCurrentTick() == call.getDurationTick()) {
				terminate();
			}
		});

		subscribeEvent(CheckAvailablilityEvent.class, call-> {
			complete(call,inventory.checkAvailabiltyAndGetPrice(call.getBook().getBookTitle()));
		});

		subscribeEvent(TakeBookEvent.class,call-> {
			complete(call,inventory.take(call.getBook().getBookTitle()));
		});
		countDownLatch.countDown();
	}

}
