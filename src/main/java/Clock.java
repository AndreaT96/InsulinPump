import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Clock {

    private Controller controller;
    private ScheduledExecutorService ses;
    private ScheduledFuture<?> scheduledFuture;

    Clock(Controller controller) {
        this.controller = controller;
    }

    public void startTasks() {
        ses = Executors.newScheduledThreadPool(2);
        scheduledFuture = ses.scheduleAtFixedRate(controller.insulinTask, 5, 1, TimeUnit.SECONDS);
        ScheduledFuture<?> scheduledFuture2 = ses.scheduleAtFixedRate(controller.insulinTask, 5, 1, TimeUnit.SECONDS);
    }

    public void stopTasks() {
        scheduledFuture.cancel(true);
        ses.shutdown();
    }

    public static LocalDateTime getTime() {
        return LocalDateTime.now();
    }
}
