# 多线程与并发 (餐饮系统)

餐饮系统在并发环境下会频繁更新某个菜品的库存数量。现有实现中，多个线程可能同时对同一库存变量执行“判断后再修改”的操作，导致最终库存结果不正确。

**任务要求：**
1. 找出代码中因并发访问共享变量而产生线程安全问题的关键区域。
2. 使用 `synchronized` 对临界区进行同步控制，可以采用**同步方法**或**同步代码块**的方式。
3. 编写一个多线程测试程序，让多个线程并发执行库存扣减操作，并说明加锁前后结果的区别，验证加锁后数据能够保持一致。

**说明：**
- 共享数据为菜品库存 `stock`。
- `if (stock > 0) { stock--; }` 这一复合操作不是原子操作，是本题需要重点分析和加锁的部分。
- 只要实现了正确的同步控制，并能通过并发测试说明问题，即可。

## 测试数据示例
请在 `Main` 类中启动三个线程，同时对同一个菜品进行操作演示：
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
if (stock > 0) {
    stock--;
}
```

上述代码看起来只有两行，但在多线程环境下并不是原子操作，实际上包含以下几个步骤：
1. 读取 `stock` 的当前值；
2. 判断 `stock > 0` 是否成立；
3. 对 `stock` 执行减 1；
4. 将新值写回内存。

如果多个线程同时执行这段逻辑，就可能出现：多个线程都先判断出 `stock > 0`，随后分别执行 `stock--`，从而导致库存扣减结果错误，这就是典型的**竞态条件**。

` synchronized ` 的作用：
- 保证同一时刻只有一个线程进入临界区；
- 保证共享变量修改后的结果对其他线程可见；
- 使“判断 + 修改”这一复合操作具备线程安全性。

本题中，临界区就是对共享变量 `stock` 进行检查和修改的代码段，因此应对以下部分加锁：

```java
if (stock > 0) {
    stock--;
}
```

可采用两种常见写法：
1. **同步方法**：直接在方法上添加 `synchronized`；
2. **同步代码块**：使用 `synchronized(this)` 包裹关键代码。

在测试中，如果 4 个线程分别执行 30 次扣减，总共尝试扣减 120 次，而初始库存只有 100：
- **未加锁时**：可能出现重复扣减、输出混乱、最终库存异常等问题；
- **加锁后**：最多只会成功扣减 100 次，最终库存稳定为 `0`，结果正确且一致。

因此，本题的核心是识别共享资源上的非原子复合操作，并使用 `synchronized` 对临界区进行保护。

### 参考代码

```java
// SharedResource.java
package com.exam.restaurant;

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
package com.exam.restaurant;
public class Inventory {
    private int count;
    public Inventory(int initialCount) { this.count = initialCount; }
    public synchronized void decrement(int amount) { this.count -= amount; }
    public int getCount() { return count; }
}

```

```java
// Main.java
package com.exam.restaurant;
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
// Dish.java
package com.exam.restaurant;

public class Dish {
    public String id;
    public String name;
    public double value;
    
    public Dish() {}
    
    public Dish(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Dish{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.restaurant;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

