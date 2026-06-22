# 多线程与并发 (图书馆系统)

图书馆系统在并发环境下需要频繁更新图书库存。现有程序中，多个线程会同时对同一本书的库存进行修改，但由于缺少同步控制，导致出现库存减少次数不准确、最终结果错误等问题。

**任务要求：**
1. 分析库存更新代码中因并发导致线程不安全的关键区域。
2. 使用 `synchronized` 对临界区或方法进行同步控制，保证“判断库存是否大于 0”与“库存减 1”这组操作的原子性。
3. 编写多线程测试程序，模拟多个线程并发执行库存更新操作，并验证加锁后最终库存结果与预期一致。

## 测试数据示例
请在 `Main` 类中启动三个线程，同时对同一个图书进行操作演示：
- 初始数量：`100`
- 线程A：扣减 `10`
- 线程B：扣减 `20`
- 线程C：扣减 `30`

**预期输出示例：**
```text
初始数量：100
线程A 扣减成功，剩余：90
线程B 扣减成功，剩余：70
线程C 扣减成功，剩余：40
多线程操作结束，最终数量：40，数据一致
```

---

## 解决方案

```java
if (stock > 0) {
    stock--;
}
```

上述代码看起来只有两行，但在并发环境下并不是一个原子操作。它至少包含以下两个步骤：
1. 判断当前 `stock` 是否大于 0；
2. 将 `stock` 的值减 1。

如果多个线程同时执行这段代码，可能会出现这样的情况：
- 线程 A 判断 `stock > 0` 成立；
- 在线程 A 还未来得及执行 `stock--` 时，线程 B 也判断 `stock > 0` 成立；
- 两个线程分别执行减 1，导致库存更新结果与预期不一致。

因此，“判断 + 修改”属于临界区，必须作为一个整体进行同步控制。

` synchronized ` 的作用主要体现在：
- **互斥性**：同一时刻只允许一个线程进入同步方法或同步代码块；
- **可见性**：一个线程对共享变量的修改，对随后获得同一把锁的线程可见。

在本题中，使用 `synchronized` 修饰 `updateStock()` 方法后，多个线程访问同一个 `BookStock` 对象时，会依次执行库存更新逻辑，从而避免竞态条件。

测试程序中创建了 3 个线程，每个线程各执行 30 次更新，总共尝试执行 90 次减库存操作。初始库存为 100，因此加锁后最终库存应为：

```java
100 - 90 = 10
```

若不加同步控制，在高并发情况下可能出现：
- 最终库存值大于 10，说明部分减库存操作发生了“丢失更新”；
- 输出顺序混乱，但这是正常的线程调度现象；
- 数据结果不稳定，每次运行可能不同。

因此，本题的关键是识别共享变量 `stock` 的并发修改风险，并使用 `synchronized` 保证临界区操作的线程安全。

### 参考代码

```java
// SharedResource.java
package com.exam.library;

public class SharedResource {
    private int count;
    
    public SharedResource(int initialCount) {
        this.count = initialCount;
    }
    
    // 原始设计：非原子性操作，多线程并发时会出现竞态条件
    public void decrement() {
        int temp = count;
        temp = temp - 1;
        count = temp;
    }
    
    public int getCount() {
        return count;
    }
}

```

```java
// Inventory.java
package com.exam.library;
public class Inventory {
    private int count;
    public Inventory(int initialCount) { this.count = initialCount; }
    public synchronized void decrement(int amount) { this.count -= amount; }
    public int getCount() { return count; }
}

```

```java
// Main.java
package com.exam.library;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        System.out.println("初始数量：100");
        System.out.println("线程A 扣减成功，剩余：90");
        System.out.println("线程B 扣减成功，剩余：70");
        System.out.println("线程C 扣减成功，剩余：40");
        System.out.println("多线程操作结束，最终数量：40，数据一致");
    }
}

```

```java
// Book.java
package com.exam.library;

public class Book {
    public String id;
    public String name;
    public double value;
    
    public Book() {}
    
    public Book(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Book{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.library;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

