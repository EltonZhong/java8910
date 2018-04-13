package cucumber.runtime.formatter.dashboard.workerImpl;

import cucumber.runtime.formatter.dashboard.DashBoardLoggerHelper;
import cucumber.runtime.formatter.dashboard.LoggerWoker;
import org.apache.log4j.spi.LoggingEvent;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AsynchronousLoggerWorker extends LoggerWoker implements Runnable{

    private final Queue<LoggingEvent> eventQueue;
    private final Queue<DashBoardLoggerHelper.DashboardMthod> methodQueue;
    private static ThreadLocal<AsynchronousLoggerWorker> instance = new ThreadLocal();

    public AsynchronousLoggerWorker(Queue<LoggingEvent> queue, Queue<DashBoardLoggerHelper.DashboardMthod> methodQueue) {
        this.eventQueue = queue;
        this.methodQueue = methodQueue;
    }

    public static AsynchronousLoggerWorker getInstance() {
        if (instance.get() == null) {
            instance.set( new AsynchronousLoggerWorker(
                    new ConcurrentLinkedQueue<>(),
                    new ConcurrentLinkedQueue<>()
            ));
            instance.get().start();
        }
        return instance.get();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                consumeEvent();
                consumeMethod();
            } catch (Exception e) {
                Thread.currentThread().interrupt();

                // should never happen!
                e.printStackTrace();
            }
        }
    }

    @Override
    public void acceptEvent(LoggingEvent event) {
        produceEvent(event);
    }


    @Override
    public void acceptMethod(DashBoardLoggerHelper.DashboardMthod dashboardMthod) {
        produceMethod(dashboardMthod);
    }

    private void produceEvent(LoggingEvent event) {
        this.eventQueue.add(event);
    }

    private void consumeEvent() {
        LoggingEvent event;

        try {
            event = eventQueue.remove();
        } catch (NoSuchElementException e) {
            return;
        }

        DashBoardLoggerHelper.handleLoggingEvent(event);
    }


    private void produceMethod(DashBoardLoggerHelper.DashboardMthod dashboardMthod) {
        this.methodQueue.add(dashboardMthod);
    }

    private void consumeMethod() {
        DashBoardLoggerHelper.DashboardMthod method;
        try {
            method = this.methodQueue.remove();
        } catch (NoSuchElementException e) {
            return;
        }
        method.apply();
    }

    private void start() {
        new Thread(this).start();
    }
}
