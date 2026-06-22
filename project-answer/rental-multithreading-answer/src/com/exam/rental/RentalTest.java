package com.exam.rental;

class CarInventory {
    private int stock = 100;

    // 使用 synchronized 修饰方法，保证扣减库存操作的原子性
    public synchronized void rentCar() {
        if (stock > 0) {
            stock--;
            System.out.println(Thread.currentThread().getName() + " 租车成功，剩余库存：" + stock);
        }
    }

    public int getStock() {
        return stock;
    }
}

public class RentalTest {
    public static void main(String[] args) throws InterruptedException {
        CarInventory inventory = new CarInventory();

        Runnable task = () -> {
            for (int i = 0; i < 30; i++) {
                inventory.rentCar();
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

        System.out.println("最终库存：" + inventory.getStock());
    }
}
```

也可以使用同步代码块实现：

```java
class CarInventory {
    private int stock = 100;

    public void rentCar() {
        synchronized (this) {
            if (stock > 0) {
                stock--;
            }
        }
    }

    public int getStock() {
        return stock;
    }
}
