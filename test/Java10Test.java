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
        w:for(int i=1;i<=4;i++){
            n:for(int j=1;j<=4;j++){
                System.out.println("i="+i+",j="+j);
                break w;
            }
        }

        int i = 0;
        do {
            if (i ++ > 10) {
                System.out.println(1);
                break;
            }
            System.out.println(i);

        } while (true);
    }
}
