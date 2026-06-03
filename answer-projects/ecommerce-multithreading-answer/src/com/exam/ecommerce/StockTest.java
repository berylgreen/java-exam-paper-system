class StockUpdater {
    private int stock = 100;

    // 方式一：同步方法
    public synchronized void updateStock() {
        if (stock > 0) {
            stock--;
            System.out.println(Thread.currentThread().getName() + " updated stock, remaining: " + stock);
        }
    }

    public int getStock() {
        return stock;
    }
}

public class StockTest {
    public static void main(String[] args) throws InterruptedException {
        StockUpdater updater = new StockUpdater();

        Runnable task = () -> {
            for (int i = 0; i < 30; i++) {
                updater.updateStock();
            }
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");
        Thread t3 = new Thread(task, "Thread-3");
        Thread t4 = new Thread(task, "Thread-4");

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        System.out.println("Final stock: " + updater.getStock());
    }
}