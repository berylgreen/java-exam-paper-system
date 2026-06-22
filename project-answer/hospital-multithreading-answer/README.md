# 多线程与并发 (医疗系统)

医疗系统在并发环境下需要频繁更新病患相关数据。现有程序中，多个线程会同时修改同一条共享数据，但代码没有进行同步控制，导致出现竞态条件，最终造成数据不一致。

**任务要求：**
1. 找出代码中因并发访问而产生问题的非原子性操作区域。
2. 使用 `synchronized` 对关键方法或代码块进行同步控制，保证同一时刻只有一个线程可以修改共享数据。
3. 编写多线程测试代码，模拟多个线程并发执行更新操作，并验证加锁前后结果的差异，说明同步机制能够保证最终数据一致性。

**说明：**
- 共享数据的典型危险操作通常是“判断 + 修改”这样的复合逻辑，例如 `if (count > 0) { count--; }`，该过程不是原子操作。
- 请给出线程安全的实现，并通过测试说明其正确性。

## 测试数据示例
请在 `Main` 类中启动三个线程，同时对同一个病历进行操作演示：
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

本题考查多线程环境下共享数据的同步控制。

在未加锁的情况下，下面这类代码会出现线程安全问题：

```java
if (count > 0) {
    count--;
}
```

原因在于这并不是一个原子操作，而是包含了多个步骤：
1. 读取 `count` 的值；
2. 判断 `count > 0` 是否成立；
3. 对 `count` 执行减 1；
4. 将结果写回内存。

当多个线程同时执行这段代码时，可能在判断完成后还未来得及修改，另一个线程也进入该逻辑，导致多个线程基于同一个旧值进行更新，从而产生数据错乱。

使用 `synchronized` 后，线程进入同步方法或同步代码块前必须先获得对象锁，因此同一时刻只能有一个线程执行这段关键代码。这样就保证了“判断 + 修改”作为一个整体执行，避免竞态条件。

本题中：
- `public synchronized void update()` 表示对当前对象加锁；
- `synchronized(this)` 与同步实例方法的锁对象相同，都是当前对象；
- `join()` 的作用是让主线程等待所有子线程执行结束，再输出最终结果，便于验证并发更新后的数据是否正确。

如果初始值为 `100`，4 个线程各执行 30 次更新，总共尝试更新 120 次。由于代码中限制了 `count > 0` 才能继续减少，因此加锁后最终结果应稳定为 `0`，不会出现负数或中间过程不一致的问题。

因此，本题的关键在于识别共享变量上的复合操作不是原子操作，并使用 `synchronized` 保证线程安全。

### 参考代码

```java
// SharedResource.java
package com.exam.hospital;

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
package com.exam.hospital;
public class Inventory {
    private int count;
    public Inventory(int initialCount) { this.count = initialCount; }
    public synchronized void decrement(int amount) { this.count -= amount; }
    public int getCount() { return count; }
}

```

```java
// Main.java
package com.exam.hospital;
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
// Patient.java
package com.exam.hospital;

public class Patient {
    public String id;
    public String name;
    public double value;
    
    public Patient() {}
    
    public Patient(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Patient{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.hospital;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

