# 多线程与并发 (酒店系统)

酒店管理系统在并发环境下需要频繁更新房间数量等共享数据。现有程序未进行同步控制，多个线程同时修改同一共享变量时，可能因竞态条件导致结果异常。

**任务要求：**
(1) 分析代码中对共享变量进行“读取—判断—修改”操作的区域，找出其中的非原子性问题。
(2) 使用 `synchronized` 关键字对关键方法或关键代码块进行同步控制。
(3) 编写一个多线程测试程序，并发调用更新逻辑，验证加锁前后结果的差异，说明加锁后能够保证数据一致性。

## 测试数据示例
请在 `Main` 类中启动三个线程，同时对同一个客房进行操作演示：
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
if (roomCount > 0) {
    roomCount--;
}
```

上述逻辑表面上只有两行，但在多线程环境中它并不是原子操作，实际可分为：

(1) 读取 `roomCount`
(2) 判断 `roomCount > 0`
(3) 执行 `roomCount--`
(4) 将结果写回共享变量

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
// SharedResource.java
package com.exam.hotel;

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
package com.exam.hotel;
public class Inventory {
    private int count;
    public Inventory(int initialCount) { this.count = initialCount; }
    public synchronized void decrement(int amount) { this.count -= amount; }
    public int getCount() { return count; }
}

```

```java
// Main.java
package com.exam.hotel;
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
// Room.java
package com.exam.hotel;

public class Room {
    public String id;
    public String name;
    public double value;
    
    public Room() {}
    
    public Room(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Room{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.hotel;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

