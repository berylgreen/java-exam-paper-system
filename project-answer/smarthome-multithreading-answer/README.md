# 多线程与并发 (智能家居)

智能家居系统需要在并发环境下频繁处理设备状态或库存类数据的更新操作。现有实现未进行任何同步控制，多个线程同时修改同一个共享变量时，可能因竞态条件导致结果错误。

**任务要求：**
1. 找出代码中因“读取—判断—修改”分离而产生线程安全问题的关键区域。
2. 使用 `synchronized` 对关键方法或关键代码块进行同步控制，保证更新操作的原子性。
3. 编写多线程测试代码，模拟多个线程同时执行更新操作，并说明加锁前后结果的差异，证明加锁后数据能够保持一致。

## 测试数据示例
请在 `Main` 类中启动三个线程，同时对同一个设备进行操作演示：
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

`stock--` 看起来是一条语句，但在 JVM 层面并不是原子操作，通常可以分为“读取当前值、计算新值、写回变量”几个步骤。多个线程同时执行时，可能在读取到相同旧值后分别完成写回，导致数据丢失更新。

本题中的线程安全问题主要出现在以下复合操作区域：

```java
if (stock > 0) {
    stock--;
}
```

这段代码包含：
1. 读取 `stock`
2. 判断 `stock > 0`
3. 执行 `stock--`

以上步骤整体不具备原子性。如果没有同步控制，多个线程可能同时通过判断，并基于相同的旧值进行修改，最终造成库存结果不准确。

使用 `synchronized` 后：
- 同一时刻只允许一个线程进入同步方法或同步代码块；
- 能保证对共享变量更新过程的原子性；
- 还能保证线程之间的可见性，即一个线程修改后的值对后续获得同一把锁的线程可见。

测试时，多个线程共同对同一个 `DeviceUpdater` 对象执行更新：
- **未加锁**：最终库存可能与理论值不一致，甚至出现重复输出相同库存值的现象；
- **加锁后**：每次减库存操作都会串行执行，最终结果与预期一致，不会出现丢失更新问题。

因此，本题的核心是识别共享变量上的非原子复合操作，并通过 `synchronized` 保证临界区线程安全。

### 参考代码

```java
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
```

也可以使用同步代码块实现：

```java
class DeviceUpdater {
    private int stock = 100;

    public void updateStock() {
        synchronized (this) {
            if (stock > 0) {
                stock--;
                System.out.println(Thread.currentThread().getName() + " 更新后库存：" + stock);
            }
        }
    }

    public int getStock() {
        return stock;
    }
}
```
