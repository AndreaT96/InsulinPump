import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This mockup class represents the clock component of the controller. It's also used to schedule the periodic tasks.
 */
public class Clock {

    private Controller controller;
    private ScheduledExecutorService ses;
    private ScheduledFuture<?> scheduledInsulinFuture;
    private ScheduledFuture<?> scheduledSystemCheckFuture;

    Clock(Controller controller) {
        this.controller = controller;
    }

    public void startTasks() {
        ses = Executors.newScheduledThreadPool(2);
        scheduledInsulinFuture = ses.scheduleAtFixedRate(controller.insulinTask, 0, 600000/Util.SPEED, TimeUnit.MILLISECONDS);
        scheduledSystemCheckFuture = ses.scheduleAtFixedRate(controller.systemCheckTask, 0, 6000/Util.SPEED, TimeUnit.MILLISECONDS);
    }

    public void stopTasks() {
        scheduledInsulinFuture.cancel(true);
        scheduledSystemCheckFuture.cancel(true);
        ses.shutdown();
    }

    public static LocalDateTime getTime() {
        return LocalDateTime.now();
    }
}
