package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch countDownLatch;

	public LogisticsService(String name, CountDownLatch countDownLatch) {
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

		subscribeEvent(DeliveryEvent.class,call->{
			Future<Future<DeliveryVehicle>> futureVehicle = sendEvent(new AcquireVehicleEvent());
			if (futureVehicle !=null) {
				if (futureVehicle.get() != null) {
					if (futureVehicle.get().get() != null) {
						DeliveryVehicle vehicle = futureVehicle.get().get();
						vehicle.deliver(call.getAddress(), call.getDistance());
						sendEvent(new ReleaseVehicle(vehicle));
						complete(call, 1);
						return;
					} else
						complete(call, null);
				} else
					complete(call, null);
			}
			else
				complete(call,null);
		});
		countDownLatch.countDown();

	}

}
