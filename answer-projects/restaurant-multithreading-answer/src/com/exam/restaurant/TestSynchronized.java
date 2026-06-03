class DishStock {
    private int stock = 100;

    // 方式1：同步方法
    public synchronized void updateStock() {
        if (stock > 0) {
            stock--;
            System.out.println(Thread.currentThread().getName()
                    + " 扣减后库存：" + stock);
        }
    }

    public int getStock() {
        return stock;
    }
}

public class TestSynchronized {
    public static void main(String[] args) throws InterruptedException {
        DishStock dishStock = new DishStock();

        Runnable task = () -> {
            for (int i = 0; i < 30; i++) {
                dishStock.updateStock();
            }
        };

        Thread t1 = new Thread(task, "线程1");
        Thread t2 = new Thread(task, "线程2");
        Thread t3 = new Thread(task, "线程3");
        Thread t4 = new Thread(task, "线程4");

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        System.out.println("最终库存：" + dishStock.getStock());
    }
}