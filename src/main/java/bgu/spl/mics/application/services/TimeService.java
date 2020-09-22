package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.Inventory;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private long tick;
	private Timer timer;
	private TimerTask timerTask;
	private int duration;
	private int speed;


	public TimeService(String name, int duration, int speed) {
		super(name);
		this.duration = duration;
		this.speed = speed;
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				tick++;
				Broadcast tickBroadcast = new TickBroadcast((int)tick,duration);
				sendBroadcast(tickBroadcast);
				if (tick == duration) {
					timerTask.cancel();
					timer.cancel();
				}
			}
		};
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class,call -> {
			if (call.getCurrentTick() == call.getDurationTick())
				terminate();
		});
		timer.schedule(timerTask,speed,speed);
	}
}