import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public class Java8Test {
    private static final List a = new ArrayList();

    static {
        a.add(1);
        a.add(30);
    }

    static List getA() {
        return a;
    }

    public static int go() {
        return s();
    }

    private static int s() {
        return 1;
    }

    public static void main(String[] args) {

        A a = String::length;
        System.out.println(a.getClass());
        System.out.println(a.valueA("1212121"));

        A j = (String s) -> {
            System.out.println(s);
            Function<String, Integer> as = (a1) -> a1.length();
            return as.apply(s);
        };
        j.valueA("1222");

        Supplier<Integer> oneReturn = () -> 1;
        oneReturn.get();

        Predicate<String> predicate = g -> g.length() > 1;
        Predicate<String> predicate1 = g -> g.length() < 10;
        System.out.println(predicate.or(predicate1).test("length"));


        Stream<String> stringStream = Arrays.stream(new String[]{"1", "2", "333333"});
        stringStream.mapToInt(str -> str.length()).forEach(inte -> System.out.println(inte));

        ManyArgs manyArgsFunction = (c, b, d) -> c + b.length() + Float.valueOf(d).intValue();
        manyArgsFunction.valueB(1, "23", 47);


    }

    /**
     * Strem 的性能优化
     * @param args
     */
    public static void main2(String[] args) {
        Arrays.stream(new int[]{1, 2, 3}).map(a -> {
            System.out.println(a + "map");
            return a;
        }).filter(a -> {
            System.out.println(a + "filter");
            return a > 0;
        }).findAny().orElse(10);
    }

    interface A {
        private void in() {
            return;
        }

        int valueA(String s);
    }

    interface ManyArgs {
        int valueB(int a, String b, final float c);
    }
}

