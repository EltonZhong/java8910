package cucumber.runtime.formatter.dashboard;

import cucumber.runtime.formatter.dashboard.workerImpl.AsynchronousLoggerWorker;
import cucumber.runtime.formatter.dashboard.workerImpl.SynchronizeLoggerWorker;
import org.apache.log4j.spi.LoggingEvent;

abstract public class LoggerWoker {

    abstract public void acceptEvent(LoggingEvent event);

    abstract public void acceptMethod(DashBoardLoggerHelper.DashboardMthod dashboardMthod);

    public static LoggerWoker getInstance() {
        if (DashBoardLoggerHelper.IS_DASHBOARD_LOGGER_MULTITHREADED) {
            return AsynchronousLoggerWorker.getInstance();
        } else {
            return SynchronizeLoggerWorker.getInstance();
        }
    }

}

