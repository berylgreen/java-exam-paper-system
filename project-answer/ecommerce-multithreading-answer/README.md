# 多线程与并发 (电商系统)

电商系统在并发环境下会同时处理大量订单更新请求。假设多个线程都会对同一个库存变量 `stock` 执行“读取当前值 → 判断是否大于 0 → 减 1”的操作，如果不进行同步控制，就可能出现竞态条件，导致最终库存结果不正确。

**任务要求：**  
(1) 指出库存更新逻辑中哪些操作属于非原子性复合操作，并说明为什么会产生线程安全问题。  
(2) 使用 `synchronized` 对关键更新逻辑进行同步控制，可以选择**同步方法**或**同步代码块**实现。  
(3) 编写一个多线程测试程序，让多个线程并发执行扣减库存操作，并说明加锁前后结果的差异，验证加锁后数据能够保持一致。

## 测试数据示例
请在 `Main` 类中启动三个线程，同时对同一个商品进行操作演示：
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

库存更新语句 `if (stock > 0) { stock--; }` 看似简单，但它不是原子操作，实际包含以下几个步骤：  

(1) 读取共享变量 `stock` 的当前值；  
(2) 判断 `stock > 0` 是否成立；  
(3) 对 `stock` 执行减 1；  
(4) 将结果写回主内存。

在多线程环境下，如果多个线程几乎同时读取到相同的 `stock` 值，就可能都通过判断，并分别执行减 1，导致**丢失更新**或结果不一致，这就是典型的竞态条件。

使用 `synchronized` 后：

- 同一时刻只允许一个线程进入同步方法或同步代码块；
- 对共享变量的复合操作可以作为一个整体执行；
- 同时还能保证线程之间的可见性。

例如：

```java
public synchronized void updateStock() {
    if (stock > 0) {
        stock--;
    }
}
```

这里 `updateStock()` 被 `synchronized` 修饰后，锁对象默认为当前实例 `this`。多个线程调用同一个对象的该方法时，必须依次执行，因此不会出现多个线程同时修改 `stock` 的情况。

测试程序中创建了多个线程并发调用 `updateStock()`。如果不加锁，最终结果可能不是预期值，甚至可能出现重复扣减判断带来的错误；加锁后，库存扣减过程具备线程安全性，最终 `stock` 的结果与实际扣减次数保持一致，且不会小于 0。

### 参考代码

```java
// SharedResource.java
package com.exam.ecommerce;

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
package com.exam.ecommerce;
public class Inventory {
    private int count;
    public Inventory(int initialCount) { this.count = initialCount; }
    public synchronized void decrement(int amount) { this.count -= amount; }
    public int getCount() { return count; }
}

```

```java
// Main.java
package com.exam.ecommerce;
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
// Order.java
package com.exam.ecommerce;

public class Order {
    public String id;
    public String name;
    public double value;
    
    public Order() {}
    
    public Order(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Order{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.ecommerce;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

