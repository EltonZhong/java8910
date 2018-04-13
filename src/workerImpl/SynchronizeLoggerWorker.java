package cucumber.runtime.formatter.dashboard.workerImpl;

import cucumber.runtime.formatter.dashboard.DashBoardLoggerHelper;
import cucumber.runtime.formatter.dashboard.LoggerWoker;
import org.apache.log4j.spi.LoggingEvent;

public class SynchronizeLoggerWorker extends LoggerWoker{

    private static final SynchronizeLoggerWorker instance = new SynchronizeLoggerWorker();

    public static SynchronizeLoggerWorker getInstance() {
        return instance;
    }

    @Override
    public void acceptEvent(LoggingEvent event) {
        DashBoardLoggerHelper.handleLoggingEvent(event);
    }

    @Override
    public void acceptMethod(DashBoardLoggerHelper.DashboardMthod dashboardMthod) {
        dashboardMthod.apply();
    }
}
