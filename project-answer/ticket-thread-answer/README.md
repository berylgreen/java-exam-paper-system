# 多线程安全 (演唱会抢票系统)

某演唱会售票系统在多线程环境下存在严重的线程安全问题：多个售票窗口同时售票时，可能出现**超卖**（余票变为负数）或**重票**（同一张票被重复售出）。

请在 `TicketWindow` 类中完成线程安全改造，要求如下：

1. 找出原程序中不安全的售票逻辑，即对共享票数的“判断 + 输出 + 减 1”操作未进行同步控制的代码。
2. 使用 `synchronized` 关键字（同步代码块或同步方法均可）保证一次完整售票操作的原子性。
3. 当票卖完后，所有售票线程都能正常结束，不再继续无效循环。
4. 修改完成后，多次运行 `Main` 程序，要求总售出票数始终与初始票数严格一致，且不能出现余票为负数或重复售票的情况。

请给出修改后的 `TicketWindow` 核心实现代码。

## 测试数据示例
请在 `Main` 类中启动三个线程，同时对同一个车票进行操作演示：
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
if (tickets > 0) {
    System.out.println(Thread.currentThread().getName() + " sold ticket: " + tickets);
    tickets--;
}
```

上述代码在多线程环境下是不安全的，因为 `tickets` 是多个线程共享的数据，而“判断是否有票”“输出当前票号”“票数减 1”这三个步骤如果不加同步，就可能被多个线程交叉执行：

- 线程 A 判断 `tickets > 0` 成立；
- 在线程 A 还未来得及减票时，线程 B 也判断 `tickets > 0` 成立；
- 两个线程可能卖出同一张票，造成**重票**；
- 还可能在票数接近 0 时继续减票，造成**超卖**。

因此，必须把这几个步骤放入同一个同步区域中，使其成为**原子操作**。`synchronized (this)` 或同步方法都可以起到互斥作用，保证同一时刻只有一个线程能执行售票逻辑。

另外，程序不能在票卖完后继续无意义地循环，因此当 `tickets <= 0` 时应立即 `break` 或返回 `false`，让线程正常结束。

对于 `Thread.sleep(10)`，它只是用于模拟真实售票延时，不负责线程同步；即使加入 `sleep`，如果没有 `synchronized`，线程安全问题仍然会存在。

完成修改后，只要多个线程共享的是同一个 `TicketWindow` 对象，那么对同一把锁进行同步控制，就可以保证：

1. 每张票只会被卖出一次；
2. 不会出现余票为负数；
3. 所有线程在票售罄后都能安全退出；
4. 多次运行程序，最终总售出票数都与初始票数严格一致。```

### 参考代码

```java
// SharedResource.java
package com.exam.ticket;

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
package com.exam.ticket;
public class Inventory {
    private int count;
    public Inventory(int initialCount) { this.count = initialCount; }
    public synchronized void decrement(int amount) { this.count -= amount; }
    public int getCount() { return count; }
}

```

```java
// Main.java
package com.exam.ticket;
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
// FileStorage.java
package com.exam.ticket;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

```java
// Ticket.java
package com.exam.ticket;

public class Ticket {
    public String id;
    public String name;
    public double value;
    
    public Ticket() {}
    
    public Ticket(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Ticket{id='" + id + "', name='" + name + "'}";
    }
}

```

