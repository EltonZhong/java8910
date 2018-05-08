import java.util.List;

public class Java10Test {

    /**
     * how to support java10 with idea?
     * idea just won't reconize java10's JDK HOME
     * resolution: download the newest version of idea
     * @param args
     */
    public static void main(String[] args) {
        var a = String.valueOf(1);
        System.out.printf("12");
        System.out.printf(a);
        System.out.println(a instanceof String);
    }
}
