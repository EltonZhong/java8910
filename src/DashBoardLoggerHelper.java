package cucumber.runtime.formatter.dashboard;

import com.ringcentral.ta.glip.util.GlipWrapper;
import com.ringcentral.tod.library.mobile.AppiumBasicSteps;
import com.ringcentral.tod.util.TodWorldSingleton;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.runtime.formatter.StepReporter;
import cucumber.runtime.java.StepDefAnnotation;
import kernel.core.Log;
import kernel.core.config.Config;
import kernel.core.integration.Modules;
import kernel.core.integration.logger.ScreenShooter;
import kernel.core.logger.additional.Container;
import kernel.core.logger.engine.LogCommon;
import kernel.core.logger.engine.LogHelper;
import kernel.core.logger.struct.ScreenShot;
import kernel.core.logger.struct.StepMarker;
import kernel.rmi.RMIServer;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DashBoardLoggerHelper {

    public static DashboardMthod initSuiteParamsMethod = () -> initSuiteParams();
    public static Function<CucumberFeatureWrapper, DashboardMthod> initTestParamsConsumer =
            (cuc) -> () -> initTestParams(cuc);
    public static DashboardMthod stopCurrentTestMethod  = () -> stopCurrentTest();
    public static final boolean IS_DASHBOARD_LOGGER_MULTITHREADED =
            "true".equals(System.getProperty("enableFastDashboardLogger"));

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DashBoardLoggerHelper.class);


    public static void initSuiteParams() {
        Log.createSuite("mThor BDD tests " + new Date(), "Host", "", "Cucumber", "", "");
        Log.getSuite().params.put("Tests/Jenkins/Job", System.getenv("JOB_NAME") == null ? "" : System.getenv("JOB_NAME"));
        Log.getSuite().params.put("Tests/Jenkins/Build", System.getenv("BUILD_NUMBER") == null ? "" : System.getenv("BUILD_NUMBER"));
        Log.getSuite().params.put("Tests/Jenkins/URL", System.getenv("JENKINS_URL") == null ? "" : System.getenv("JENKINS_URL"));
        Log.getSuite().params.put("Tests/Jenkins/Name", System.getenv("NODE_NAME") == null ? "" : System.getenv("NODE_NAME"));
    }


    public static void initTestParams(CucumberFeatureWrapper cucumberFeatureWrapper) {
        try {
            Log.setTest(LogCommon.createTest(
                    cucumberFeatureWrapper.getCucumberFeature().getGherkinFeature().getFeature().getName()
                            + ": " + new Date(),
                    cucumberFeatureWrapper.getCucumberFeature().getGherkinFeature().getComments().toString(),
                    cucumberFeatureWrapper.getCucumberFeature().getGherkinFeature().getFeature().getDescription()
                            != null
                            ? cucumberFeatureWrapper.getCucumberFeature().getGherkinFeature().getFeature().getDescription()
                            : "",
                    "",
                    1,
                    new String[0]));

        } catch (Exception e) {
            logger.error("Init testcase in dashboard failed", e);
        }
    }


    public static void stopCurrentTest() {
        Log.stopTest(Log.getCurrentTest());
    }


    public static void initDashboard() {

        initDashboardProperties();

        try {
            initScreenShooter();
        } catch (Exception e) {
            logger.error("Init screenshooter failed", e);
        }

        initMoudules();

        // Remove ConsoleAppender of BasicConfigurator
        for (Enumeration e = Logger.getRootLogger().getAllAppenders(); e.hasMoreElements(); ) {
            Object obj = e.nextElement();
            if (obj instanceof ConsoleAppender && ((ConsoleAppender) obj).getName() == null) {
                Logger.getRootLogger().removeAppender((Appender) obj);
            }
        }
    }


    /**
     * Remove ConsoleAppender of kenerl.core.log
     * Fix bug for automation core
     */
    public static void prepareForAutCore() {

        // No meaning, in case that the bug in kernel.log affects the system when connection to dashboard is down
        System.setProperty("ScreenShot", System.getProperty("ScreenShot", "screenshot"));

        // Fix bug for class 'GeneratedMethodAccessor' not found
        Container.ignoreList.add("sun.reflect.GeneratedMethodAccessor");

        try {
            Field field = LogCommon.class.getDeclaredField("appenders");
            field.setAccessible(true);
            java.util.List list = (List<Object>) field.get(null);
            field.set(null, list.stream()
                    .filter(appender -> appender.getClass() != kernel.core.logger.appenders.Console.class)
                    .collect(Collectors.toList()));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("Remove ConsoleAdapater of kenerl.log failed", e);
            return;
        }
    }

    public static void handleLoggingEvent(LoggingEvent event) {
        Throwable throwable = event.getThrowableInformation() == null ? null :
                event.getThrowableInformation().getThrowable();

        try {
            Arrays.stream(LogHelper.class.getDeclaredMethods())
                    .filter(method -> method.getName().equals(event.getLevel().toString().toLowerCase()))
                    .filter(method -> method.getParameterCount() == 2) // get Logger.debug(String, Throwable)
                    .findAny()
                    .get()
                    .invoke(null, event.getMessage(), throwable);

        } catch (NoSuchElementException e) {
            if (event.getLevel().equals(Level.INFO)) {
                LogHelper.info((String) event.getMessage());
            } else {
                LogHelper.warning((String) event.getMessage());
            }

        } catch (Exception e) {
            // TODO
        }
    }

    private static void initScreenShooter() throws Exception {
        ScreenShooter screenShooter = new ScreenShooter(null,
                new RMIServer(),
                Config.getConfig().screenshotFormat);
        screenShooter.setScreenShotMethod(() -> {
            byte[] bytes = {0xa, 0x2, 0xf, (byte) 0xff, (byte) 0xff, (byte) 0xff};

            try {
                if (null != TodWorldSingleton.getInstance().getItems().get(AppiumBasicSteps.MOBILE_INSTANCE)) {
                    if (GlipWrapper.getMobileDevice() != null) {
                        TakesScreenshot driver = GlipWrapper.getMobileDevice().getAppiumDriver();
                        if (driver != null) {
                            bytes = driver.getScreenshotAs(OutputType.BYTES);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Capture screenshot failed", e);
            }
            return bytes;
        });

        Log.addShooter(screenShooter);

        setCaptureConditon();
    }


    private static void initDashboardProperties() {
        logger.info("Initialising dashboard properties....");

        System.setProperty("Dashboard.DB.URL",
                System.getProperty("Dashboard.DB.URL", "jdbc:postgresql://10.62.13.0:5432/"));
        logger.debug("Dashboard.DB.URL = " + System.getProperty("Dashboard.DB.URL"));
        System.setProperty("Dashboard.DB.Driver",
                System.getProperty("Dashboard.DB.Driver", "org.postgresql.Driver"));
        logger.debug("Dashboard.DB.Driver = " + System.getProperty("Dashboard.DB.Driver"));
        System.setProperty("Dashboard.DB.Name",
                System.getProperty("Dashboard.DB.Name", "dashboard"));
        logger.debug("Dashboard.DB.Name = " + System.getProperty("Dashboard.DB.Name"));
        System.setProperty("Dashboard.DB.User",
                System.getProperty("Dashboard.DB.User", "dashboard"));
        logger.debug("Dashboard.DB.User = " + System.getProperty("Dashboard.DB.User"));
        System.setProperty("Dashboard.DB.Pass",
                System.getProperty("Dashboard.DB.Pass", "dashboard"));
        logger.debug("Dashboard.DB.Pass = " + System.getProperty("Dashboard.DB.Pass"));

        logger.info("Dashboard properties initialised");
    }


    private static void setCaptureConditon() {
        ScreenShot.setScreenShotSelenium("true");
        ScreenShot.setScreenShotRMI(
                StepMarker.FATAL, StepMarker.FAILED, StepMarker.WARNING, StepMarker.PASSED, StepMarker.ERROR
        );

        // Only when method stacks contains stepdefinition
        LogCommon.setCaptureBehaviour((stack) -> {
            if (DashBoardLoggerHelper.IS_DASHBOARD_LOGGER_MULTITHREADED) {
                return true;
            }
            try {
                return Arrays.stream(Arrays.stream(Class.forName(stack.getClassName()).getDeclaredMethods())
                        .filter(m -> m.getName().equals(stack.getMethodName()))
                        .findAny()
                        .get()
                        .getAnnotations())
                        .anyMatch(a -> a.annotationType().getAnnotation(StepDefAnnotation.class) != null)
                        || stack.getClassName().contains(StepReporter.class.getSimpleName());
            } catch (Exception e) {
                return false;
            }
        });
    }


    private static void initMoudules() {

        // Will bring unexpected ConsoleAppender to BasicConfigurator
        Modules.init();

        // Level is set to off in Modules.init()
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    public interface DashboardMthod {
        void apply();
    }

}
