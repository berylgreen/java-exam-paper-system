# 多态与继承 (电商系统)

某电商系统中有一个体量较大的统计处理类，当前代码通过连续的 `if (obj instanceof X) ... else if (obj instanceof Y) ...` 来区分不同类型的订单对象，并执行各自的处理逻辑。这种写法会导致代码可读性差、扩展困难，也不利于后续维护。

请使用**面向对象中的多态**思想对其进行重构，要求如下：

1. 定义一个通用的 `Order` 抽象类或接口，并声明统一的处理方法；
2. 为不同类型的订单创建对应的子类，并在子类中实现各自的处理逻辑；
3. 编写统计处理类，基于多态调用订单对象的方法，彻底消除显式的类型判断语句（如 `instanceof`）。

请给出核心代码实现，并体现出“新增订单类型时，原有统计处理类无需修改”的设计思想。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础商品对象：`笔记本电脑`
- 创建高级商品对象：`智能手机`
- 使用统一方法遍历调用处理逻辑

### 预期输出示例
```text
统一处理基础商品：笔记本电脑
统一处理高级商品：智能手机
```

---

## 解决方案

本题的核心是使用**继承（或接口）+ 方法重写 + 多态**来替代大量的类型判断。

### 设计思路
1. **抽象父类 `Order`**
   - 把所有订单共有的“处理行为”抽取为统一方法 `process()`；
   - 父类只规定规范，不关心具体实现。

2. **具体子类分别实现自己的逻辑**
   - `RegularOrder`、`VIPOrder`、`GroupOrder` 等子类各自重写 `process()`；
   - 不同订单的差异化行为由各自子类负责完成。

3. **统计处理类只面向父类编程**
   - `OrderStatistics` 中的方法参数使用 `Order[]`；
   - 遍历时直接调用 `order.process()`，Java 会在运行时根据对象真实类型执行对应子类的方法，这就是多态。

### 为什么这种写法优于 `instanceof`
若使用下面这种方式：

```java
if (obj instanceof RegularOrder) {
    // ...
} else if (obj instanceof VIPOrder) {
    // ...
} else if (obj instanceof GroupOrder) {
    // ...
}
```

会带来以下问题：
- 每增加一种订单类型，都要修改原有统计类；
- 条件分支越来越多，代码臃肿；
- 不符合**开闭原则**（对扩展开放、对修改关闭）。

而使用多态后：
- 新增订单类型时，只需新增一个继承 `Order` 的子类；
- `OrderStatistics` 无需修改；
- 程序结构更清晰，职责划分更合理。

### 总结
本题通过将“不同订单的不同处理逻辑”分散到各自子类中，实现了：
- 消除 `instanceof` 和大量 `if...else`；
- 提高代码可维护性与扩展性；
- 更好地体现面向对象编程中的多态特性。

### 参考代码

```java
abstract class Order {
    /**
     * 订单处理的统一入口
     */
    public abstract void process();
}

class RegularOrder extends Order {
    @Override
    public void process() {
        System.out.println("处理普通订单");
    }
}

class VIPOrder extends Order {
    @Override
    public void process() {
        System.out.println("处理 VIP 订单");
    }
}

class GroupOrder extends Order {
    @Override
    public void process() {
        System.out.println("处理团购订单");
    }
}

public class OrderStatistics {
    public void processAll(Order[] orders) {
        for (Order order : orders) {
            order.process(); // 利用多态调用具体子类的实现
        }
    }

    public static void main(String[] args) {
        Order[] orders = {
            new RegularOrder(),
            new VIPOrder(),
            new GroupOrder()
        };

        OrderStatistics statistics = new OrderStatistics();
        statistics.processAll(orders);
    }
}
```
