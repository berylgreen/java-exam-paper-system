# 多线程与并发 (酒店系统)

酒店管理系统在并发环境下需要频繁更新房间数量等共享数据。现有程序未进行同步控制，多个线程同时修改同一共享变量时，可能因竞态条件导致结果异常。

**任务要求：**
1. 分析代码中对共享变量进行“读取—判断—修改”操作的区域，找出其中的非原子性问题。
2. 使用 `synchronized` 关键字对关键方法或关键代码块进行同步控制。
3. 编写一个多线程测试程序，并发调用更新逻辑，验证加锁前后结果的差异，说明加锁后能够保证数据一致性。


---

## 解决方案

```java
if (roomCount > 0) {
    roomCount--;
}
```

上述逻辑表面上只有两行，但在多线程环境中它并不是原子操作，实际可分为：

1. 读取 `roomCount`
2. 判断 `roomCount > 0`
3. 执行 `roomCount--`
4. 将结果写回共享变量

如果多个线程同时进入这段代码，可能在判断时都看到 `roomCount > 0`，随后分别执行减一，导致出现“超卖”或最终结果不准确的问题，这就是典型的竞态条件。

使用 `synchronized` 后，同一时刻只允许一个线程进入被锁定的方法或代码块，从而保证：

- **原子性**：读取、判断、修改作为一个整体执行，不会被其他线程打断。
- **可见性**：一个线程对共享变量的修改，对随后获得同一把锁的线程可立即可见。
- **有序性**：同步块内的操作按程序顺序执行，减少并发下的指令重排影响。

在测试程序中，多个线程并发执行 `bookRoom()`：

- **未加锁时**：最终剩余房间数可能异常，甚至出现逻辑错误。
- **加锁后**：最多只能成功预订 100 次，最终 `roomCount` 一定不会小于 0，结果保持一致。

因此，本题的关键是识别对共享变量的复合操作属于临界区，并使用 `synchronized` 对临界区进行保护。

### 参考代码

```java
class RoomManager {
    private int roomCount = 100;

    // 方法级同步
    public synchronized void bookRoom() {
        if (roomCount > 0) {
            roomCount--;
            System.out.println(Thread.currentThread().getName() + " 预订成功，剩余房间：" + roomCount);
        } else {
            System.out.println(Thread.currentThread().getName() + " 预订失败，房间已满");
        }
    }

    public int getRoomCount() {
        return roomCount;
    }
}

public class TestRoomManager {
    public static void main(String[] args) throws InterruptedException {
        RoomManager manager = new RoomManager();

        Runnable task = () -> {
            for (int i = 0; i < 30; i++) {
                manager.bookRoom();
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

        System.out.println("最终剩余房间数：" + manager.getRoomCount());
    }
}
```

也可以使用同步代码块实现：

```java
class RoomManager {
    private int roomCount = 100;

    public void bookRoom() {
        synchronized (this) {
            if (roomCount > 0) {
                roomCount--;
                System.out.println(Thread.currentThread().getName() + " 预订成功，剩余房间：" + roomCount);
            } else {
                System.out.println(Thread.currentThread().getName() + " 预订失败，房间已满");
            }
        }
    }

    public int getRoomCount() {
        return roomCount;
    }
}
```
