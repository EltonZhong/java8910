package cucumber.runtime.formatter.dashboard;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class DashboardAppender extends AppenderSkeleton {

    private static org.slf4j.Logger tempLogger = LoggerFactory.getLogger(DashboardAppender.class);
    private final LoggerWoker loggerWorker = LoggerWoker.getInstance();

    public DashboardAppender() {
        DashBoardLoggerHelper.prepareForAutCore();

        if (System.getProperty("enableDashboard", "false").equals("true") && isAbleToCaptureScreenShot()) {
            tempLogger.info("Dashboard is enabled");
            DashBoardLoggerHelper.initDashboard();
        } else {
            tempLogger.info("Dashboard is not enabled");
        }

        tempLogger.info("DashAdapter init successfully");
    }


    @Override
    protected void append(LoggingEvent event) {
        if (event.getLoggerName().contains(DashboardAppender.class.getPackage().getName())) {
            return;
        }

        loggerWorker.acceptEvent(event);
    }

    @Override
    public void close() {

    }


    @Override
    public boolean requiresLayout() {
        return false;
    }


    private static boolean isAbleToCaptureScreenShot() {
        if (GraphicsEnvironment.isHeadless()) {
            tempLogger.warn("Server is headless, can not capture screenshot");
            return false;
        }
        tempLogger.info("Server is not headless and is able to capture screenshot");
        return true;
    }
}

