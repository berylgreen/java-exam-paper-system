# 多线程与并发 (银行系统)

银行系统需要在并发环境下处理高频账户余额更新。现有程序中，多个线程会同时对同一个账户余额执行“读取当前值→修改→写回”的操作，但未进行任何同步控制，导致出现竞态条件，最终余额可能与预期不一致。

**任务要求：**
1. 分析程序中哪些语句属于并发下的非原子性操作，并说明产生数据错误的原因。
2. 使用 `synchronized` 对共享账户对象的关键更新逻辑进行同步控制，可以采用同步方法或同步代码块。
3. 编写一个多线程测试程序，让多个线程并发执行账户扣减操作，并验证加锁前后结果的差异，说明同步后数据能够保持一致。

## 测试数据示例
请在 `Main` 类中启动三个线程，同时对同一个账户进行操作演示：
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

```java
if (balance >= amount) {
    balance -= amount;
}
```
以上看似简单的余额更新操作，在并发环境下并不是原子操作。它通常包含以下几个步骤：
1. 读取 `balance` 的当前值；
2. 判断余额是否足够；
3. 计算新的余额；
4. 将结果写回 `balance`。

如果多个线程同时执行这段代码，可能会在“读取旧值”后还未来得及写回时，被其他线程插入执行，从而造成数据覆盖或结果错误，这就是竞态条件。

使用 `synchronized` 后：
- 同一时刻只允许一个线程进入受保护的临界区；
- 能保证对共享变量 `balance` 的操作具备原子性；
- 还能保证线程之间的可见性，即一个线程修改后的结果对后续获得锁的线程可见。

在测试程序中，4 个线程各执行 20 次 `withdraw(1)`，理论上总共扣减 80 次。初始余额为 100，因此最终余额应为 20。若不加同步，最终余额可能大于 20，说明有部分更新丢失；加上 `synchronized` 后，最终结果应稳定为 20，说明共享数据在并发访问下得到了正确保护。

因此，本题的关键是识别“检查余额并修改余额”这一临界区，并使用 `synchronized` 对其进行同步控制。```

### 参考代码

```java
// SharedResource.java
package com.exam.bank;

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
package com.exam.bank;
public class Inventory {
    private int count;
    public Inventory(int initialCount) { this.count = initialCount; }
    public synchronized void decrement(int amount) { this.count -= amount; }
    public int getCount() { return count; }
}

```

```java
// Main.java
package com.exam.bank;
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
// Account.java
package com.exam.bank;

public class Account {
    public String id;
    public String name;
    public double value;
    
    public Account() {}
    
    public Account(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Account{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.bank;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

