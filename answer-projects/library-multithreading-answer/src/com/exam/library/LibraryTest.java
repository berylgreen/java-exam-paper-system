class BookStock {
    private int stock = 100;

    // 使用 synchronized 修饰方法，保证同一时刻只有一个线程能执行该方法
    public synchronized void updateStock() {
        if (stock > 0) {
            stock--;
            System.out.println(Thread.currentThread().getName() + " 更新后库存：" + stock);
        }
    }

    public int getStock() {
        return stock;
    }
}

public class LibraryTest {
    public static void main(String[] args) throws InterruptedException {
        BookStock bookStock = new BookStock();

        Runnable task = () -> {
            for (int i = 0; i < 30; i++) {
                bookStock.updateStock();
            }
        };

        Thread t1 = new Thread(task, "线程1");
        Thread t2 = new Thread(task, "线程2");
        Thread t3 = new Thread(task, "线程3");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("最终库存：" + bookStock.getStock());
    }
}