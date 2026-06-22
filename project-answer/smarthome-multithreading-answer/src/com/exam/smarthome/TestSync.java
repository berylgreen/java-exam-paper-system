package com.exam.smarthome;

class DeviceUpdater {
    private int stock = 100;

    // 方法级同步
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

public class TestSync {
    public static void main(String[] args) throws InterruptedException {
        DeviceUpdater updater = new DeviceUpdater();

        Runnable task = () -> {
            for (int i = 0; i < 30; i++) {
                updater.updateStock();
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

        System.out.println("最终库存：" + updater.getStock());
    }
}