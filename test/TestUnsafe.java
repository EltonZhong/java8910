import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;

public class TestUnsafe {
    /**
     * unsafe is an unsafe and super fast lib to 直接操作内存
     * <a href = "http://www.baeldung.com/java-unsafe">document</a>
     * @param args
     */
    public static void main(String[] args) throws Throwable{

    }

    private static void allocateNewClassWithoutConstructing() throws Throwable{
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        ConstructMe constructMe = (ConstructMe) unsafe.allocateInstance(ConstructMe.class);

    }

    private static void throwUnsafeExecption() throws NoSuchFieldException, IllegalAccessException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);

        unsafe.throwException(new IOException());
    }

    class ConstructMe {
        public ConstructMe() throws Exception {
            System.getLogger("logger").log(System.Logger.Level.INFO,
                    () -> {
                        System.out.println("hook");
                        return "1";
                    }, new Exception());

            throw new Exception();
        }
    }

    /**
     * 直接在非堆上创建对象， 不受gc控制
     */
    class OffHeapArray {
        private final static int BYTE = 1;
        private long size;
        private long address;

        public OffHeapArray(long size) throws NoSuchFieldException, IllegalAccessException {
            this.size = size;
            address = getUnsafe().allocateMemory(size * BYTE);
        }

        private Unsafe getUnsafe() throws IllegalAccessException, NoSuchFieldException {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        }

        public void set(long i, byte value) throws NoSuchFieldException, IllegalAccessException {
            getUnsafe().putByte(address + i * BYTE, value);
        }

        public int get(long idx) throws NoSuchFieldException, IllegalAccessException {
            return getUnsafe().getByte(address + idx * BYTE);
        }

        public long size() {
            return size;
        }

        public void freeMemory() throws NoSuchFieldException, IllegalAccessException {
            getUnsafe().freeMemory(address);
        }
    }
}
