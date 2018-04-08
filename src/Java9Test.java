import javafx.util.Pair;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public class Java9Test extends Java8Test{
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
    }
}
