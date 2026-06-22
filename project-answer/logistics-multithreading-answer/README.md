# 多线程与并发 (物流系统)

物流系统需要在并发环境下处理大量包裹库存更新操作。现有实现未进行任何同步控制，多个线程可能同时修改同一条库存记录，导致出现数据不一致的问题。

**任务要求：**
1. 分析库存更新代码中哪些语句属于并发下的非原子性操作。
2. 使用 `synchronized` 对关键更新逻辑进行同步控制，可以采用**同步方法**或**同步代码块**。
3. 编写多线程测试代码，模拟多个线程并发执行库存扣减操作，并说明加入同步后为什么能够保证最终结果正确。

## 测试数据示例
请在 `Main` 类中启动三个线程，同时对同一个包裹进行操作演示：
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

`stock--` 看起来是一条语句，但在底层并不是原子操作，通常会包含“读取 stock 的值 → 计算减 1 → 写回新值”三个步骤。

在没有同步控制时，多个线程可能同时通过 `if (stock > 0)` 判断，并基于同一个旧值进行减 1，导致：
- 库存少减或重复覆盖；
- 最终结果与理论值不一致；
- 输出顺序混乱，表现出明显的竞态条件。

### 关键的非原子性区域
库存更新逻辑中的以下代码必须作为一个整体进行保护：

```java
if (stock > 0) {
    stock--;
}
```

原因是：判断和修改之间不能被其他线程插入执行，否则就会发生线程安全问题。

### `synchronized` 的作用
给方法或代码块加上 `synchronized` 后，同一时刻只允许一个线程进入受保护区域：
- 保证了**互斥性**：避免多个线程同时修改共享变量；
- 保证了**可见性**：一个线程修改后的 `stock` 对后续获得锁的线程可见。

### 为什么测试后结果正确
测试程序中共创建 4 个线程，每个线程执行 30 次扣减，总共尝试扣减 120 次，而初始库存为 100。

加入同步后：
- 每次只有一个线程能执行 `if (stock > 0) { stock--; }`；
- 当库存减到 0 后，后续线程无法再进入扣减分支；
- 因此最终 `stock` 不会小于 0，最后结果稳定为 `0`。

如果不加同步，则可能出现多个线程同时读取到相同库存值并重复写回，导致最终库存大于 0，说明数据不一致。

因此，本题的核心是识别共享变量 `stock` 的复合操作并使用 `synchronized` 将其变为线程安全的临界区。

### 参考代码

```java
class StockUpdater {
    private int stock = 100;

    // 方式一：同步方法
    public synchronized void updateStock() {
        if (stock > 0) {
            stock--;
            System.out.println(Thread.currentThread().getName() + " updated stock: " + stock);
        }
    }

    public int getStock() {
        return stock;
    }
}

public class TestStockUpdater {
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
```

也可以使用同步代码块：

```java
class StockUpdater {
    private int stock = 100;

    public void updateStock() {
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
