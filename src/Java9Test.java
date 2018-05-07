import javafx.util.Pair;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public class Java9Test extends Java8Test {
    static Pair p = new Pair(1, 2);


    private int s() {
        return 0;
    }


    public static void main(String[] args) {
        System.out.println(p);
        try {
            Field a1 = Java8Test.class.getDeclaredField("a");
            a1.setAccessible(true);
            System.out.println(a1.get(null));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Java9Test.getA().add(12);
        System.out.println(Java8Test.getA());
        System.out.println(Java8Test.getA() == Java9Test.getA());


        System.out.println(go());

        System.getLogger("Simpe").log(System.Logger.Level.INFO, "12", new Exception());
        try {
            Class.forName("sun.reflect.");
        } catch (Exception e) {
            e.printStackTrace();
        }


        while (true) {
            if (false) {
                System.out.println();
            }
            try {
                System.out.println(getProcessCpuLoad());
            } catch (Exception E) {

            }
        }
    }

    public static double getProcessCpuLoad() throws Exception {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

        if (list.isEmpty()) {
            return Double.NaN;
        }

        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0) {
            return Double.NaN;
        }
        // returns a percentage value with 1 decimal point precision
        return ((int) (value * 1000) / 10.0);
    }
}
