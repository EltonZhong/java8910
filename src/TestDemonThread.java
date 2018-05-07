public class TestDemonThread {
    /**
     * Java thread  没有父子, 却抽象出了守护线程
     * @param args
     */
    public static void main(String[] args) {

        Thread e = new Thread(() -> {
            while (true) {
                System.out.println(1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });

        System.out.println(e.isDaemon());
        e.setDaemon(true);
        e.start();
        System.out.println(e.isDaemon());
    }
}
