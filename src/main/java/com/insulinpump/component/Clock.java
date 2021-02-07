package com.insulinpump.component;

import com.insulinpump.Controller;
import com.insulinpump.Util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This mockup class represents the clock component of the controller. It's also used to schedule the periodic tasks.
 */
public class Clock {

    private final Controller controller;
    private ScheduledExecutorService ses;
    private ScheduledFuture<?> scheduledInsulinFuture;
    private ScheduledFuture<?> scheduledSystemCheckFuture;
    private ScheduledFuture<?> scheduledDisplayRefreshFuture;
    private LocalDateTime lastRealTime;
    private LocalDateTime lastReadTime;

    /**
     * Constructs a Clock object which is able to schedule all the required tasks.
     * @param controller the controller which contains the tasks to schedule
     */
    public Clock(Controller controller) {
        this.controller = controller;
        this.lastReadTime = LocalDateTime.now();
        this.lastRealTime = LocalDateTime.now();
    }

    /**
     * Starts the 3 tasks (Insulin Task, System Check Task, Display Refresh Task).
     * The speed is determined by the config field Util.SPEED
     */
    public void startTasks() {
        ses = Executors.newScheduledThreadPool(2);
        scheduledInsulinFuture = ses.scheduleAtFixedRate(controller.insulinTask, 0, 600000/ Util.SPEED, TimeUnit.MILLISECONDS);
        scheduledSystemCheckFuture = ses.scheduleAtFixedRate(controller.systemCheckTask, 0, 6000/Util.SPEED, TimeUnit.MILLISECONDS);
        if(scheduledDisplayRefreshFuture == null)
            scheduledDisplayRefreshFuture = ses.scheduleAtFixedRate(controller.refreshDisplays, 0, 17, TimeUnit.MILLISECONDS);
    }

    /**
     * Asks the scheduler to cancel the tasks (except the display refresh one) and shuts down the executor.
     */
    public void stopTasks() {
        scheduledInsulinFuture.cancel(true);
        scheduledSystemCheckFuture.cancel(true);
        ses.shutdown();
    }

    /**
     * This method calculates the correct time at the time of its invocation based on the clock simulation.
     * If SPEED is 1, it'll simply return the current REAL WORLD time.
     * If SPEED is 100, if you start the application at 16:00 (real world) and at 16:01 you call this method, it'll tell you that the time is 17:40 (100 minutes since 16:00)
     * @return the time (accelerated by Util.SPEED)
     */
    public LocalDateTime getTime() {
        LocalDateTime now = LocalDateTime.now();
        lastReadTime = lastReadTime.plusNanos(ChronoUnit.NANOS.between(lastRealTime,now)*Util.SPEED);
        lastRealTime = now;
        System.out.println(lastReadTime);
        return  lastReadTime;
    }
}
