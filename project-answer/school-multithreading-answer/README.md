# 多线程与并发 (教务系统)

教务系统在并发环境下需要频繁处理学生信息的更新操作。现有实现中，多个线程会同时修改同一条学生记录，由于缺少同步控制，导致共享数据出现不一致的问题。

**任务要求：**
1. 分析程序中哪些对共享数据的操作属于并发下的非原子性操作。
2. 使用 `synchronized` 对关键方法或关键代码块进行同步保护，避免竞态条件。
3. 编写一个多线程测试程序，模拟多个线程并发执行更新操作，并通过运行结果说明：加入同步后，共享数据能够保持最终一致。


---

## 解决方案

`score--` 看似是一条语句，但在 JVM 层面通常包含“读取当前值、计算新值、写回主内存”等多个步骤，因此它不是原子操作。在多线程环境下，如果多个线程同时执行该语句，就可能出现丢失更新，导致最终结果错误。

本题中的关键并发风险区域是对共享变量 `score` 的判断与修改：

```java
if (score > 0) {
    score--;
}
```

这段逻辑包含“判断”和“修改”两个相关步骤，如果不加同步，可能出现以下情况：
- 线程 A 判断 `score > 0` 成立；
- 在线程 A 还未来得及写回结果前，线程 B 也判断成立；
- 两个线程都执行减 1，造成结果与预期不符。

使用 `synchronized` 后，同一时刻只能有一个线程进入同步方法或同步代码块，从而保证：
1. **原子性**：判断和修改作为一个整体执行，不会被其他线程打断；
2. **可见性**：一个线程修改后的结果，对随后获得锁的线程立即可见；
3. **有序性**：线程进入和退出同步块时，相关操作具备一定的内存语义约束。

测试程序中创建了多个线程并发调用 `updateScore()`。加入同步后，虽然线程执行顺序仍然是不确定的，但每次对 `score` 的更新都会被串行化处理，因此最终结果是可预测且一致的。

例如，初始分数为 `100`，3 个线程各执行 30 次更新，总共尝试更新 90 次。在同步保护下，最终结果应为：

```java
最终分数：10
```

这说明共享数据在并发环境下得到了正确保护。

### 参考代码

```java
class StudentRecord {
    private int score = 100;

    // 对共享数据的修改使用 synchronized 进行同步
    public synchronized void updateScore() {
        if (score > 0) {
            score--;
            System.out.println(Thread.currentThread().getName() + " 更新后分数：" + score);
        }
    }

    public int getScore() {
        return score;
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        StudentRecord record = new StudentRecord();

        Runnable task = () -> {
            for (int i = 0; i < 30; i++) {
                record.updateScore();
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

        System.out.println("最终分数：" + record.getScore());
    }
}
```

也可以使用同步代码块实现：

```java
public void updateScore() {
    synchronized (this) {
        if (score > 0) {
            score--;
        }
    }
}
```
