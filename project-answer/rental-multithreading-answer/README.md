# 多线程与并发 (租车系统)

某租车系统在并发环境下需要频繁更新车辆库存。现有实现中，多个线程会同时修改同一车辆的库存数量，但代码没有进行同步控制，导致出现竞态条件，最终造成库存数据不一致。

请完成以下任务：
1. 找出库存更新逻辑中由于“读取—判断—修改”分离而导致的非原子性操作区域。
2. 使用 `synchronized` 关键字对关键方法或关键代码块进行同步控制，保证同一时刻只有一个线程可以执行库存更新操作。
3. 编写一个多线程测试程序，模拟多个线程同时执行库存扣减，说明未加锁时可能出现数据错误，而加锁后最终结果能够保持一致。

## 测试数据示例
请在 `Main` 类中启动三个线程，同时对同一个车辆进行操作演示：
- 初始数量：`100`
- 线程A：扣减 `10`
- 线程B：扣减 `20`
- 线程C：扣减 `30`

### 预期输出示例
```text
初始数量：100
线程A 扣减成功，剩余：90
线程B 扣减成功，剩余：70
线程C 扣减成功，剩余：40
多线程操作结束，最终数量：40，数据一致
```

---

## 解决方案

库存扣减操作看似只有一条 `stock--` 语句，但它本质上不是原子操作，而是包含以下几个步骤：
1. 读取 `stock` 当前值；
2. 判断 `stock > 0` 是否成立；
3. 对 `stock` 执行减 1；
4. 将新值写回内存。

在多线程环境下，如果多个线程同时执行这段逻辑，可能会在“读取”和“写回”之间相互干扰。例如两个线程都先读到同一个库存值 1，然后分别判断都大于 0，最后都执行减 1，就可能导致结果错误。这种由于线程竞争共享资源而产生的问题就是竞态条件。

使用 `synchronized` 后：
- 同一时刻只能有一个线程进入同步方法或同步代码块；
- 库存的“判断 + 修改”操作变成一个不可分割的临界区；
- 可以保证操作的原子性，并且还具有可见性效果，使一个线程修改后的结果能够被其他线程及时看到。

在测试程序中，多个线程同时调用 `rentCar()`：
- 如果不加锁，最终库存可能与预期不一致，甚至出现重复扣减等问题；
- 如果加上 `synchronized`，线程会按顺序进入临界区执行，最终库存结果就会与实际扣减次数保持一致。

因此，本题的关键是识别共享变量 `stock` 的临界区，并通过 `synchronized` 对其进行保护，从而保证并发更新时数据正确。

### 参考代码

```java
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
```
