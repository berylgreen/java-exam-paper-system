# 多态与继承 (餐饮系统)

某餐饮系统中有一个职责过于集中的统计类，它通过大量的 `if (obj instanceof X) ... else if (obj instanceof Y) ...` 语句来区分并处理不同类型的菜品对象。这种实现方式可扩展性较差：每增加一种新菜品类型，都需要修改统计类中的判断逻辑。

请使用**面向对象中的多态思想**对该设计进行重构，要求如下：
1. 定义一个通用的 `Dish` 接口或抽象类，声明统一的抽象行为。
2. 编写多个具体菜品子类，在各自的类中实现对应的处理逻辑。
3. 在统计类中仅面向 `Dish` 编程，通过多态调用完成统一处理，彻底消除显式的类型判断语句。

请给出核心代码实现，并体现重构后的调用方式。


---

## 解决方案

本题的核心是用**多态**替代 `instanceof` + `if...else` 的分支判断。

### 1. 原有问题
如果统计类中写成下面这种形式：

```java
if (obj instanceof RegularDish) {
    // 处理普通菜品
} else if (obj instanceof VIPDish) {
    // 处理 VIP 菜品
} else if (obj instanceof DiscountDish) {
    // 处理特价菜品
}
```

那么每新增一种菜品类型，都必须修改统计类代码，这会带来两个问题：
- 统计类职责过重，既要负责遍历，又要负责识别每种类型；
- 不符合**开闭原则**：对扩展不够开放，因为新增子类时需要修改旧代码。

### 2. 重构思路
先将“不同菜品各自不同的处理逻辑”下放到具体子类中：
- 父类 `Dish` 只定义统一方法 `process()`；
- 每个子类根据自身特点重写 `process()`；
- 统计类只需要遍历 `Dish` 对象并调用 `process()`，无需知道对象的真实类型。

### 3. 多态的体现
在下面这句代码中：

```java
dish.process();
```

虽然 `dish` 的编译时类型是 `Dish`，但运行时会根据对象的实际类型调用对应子类的方法：
- `RegularDish` 对象调用 `RegularDish` 的 `process()`；
- `VIPDish` 对象调用 `VIPDish` 的 `process()`；
- `DiscountDish` 对象调用 `DiscountDish` 的 `process()`。

这就是**运行时多态**。

### 4. 重构后的优点
- **消除类型判断**：不再需要 `instanceof`。
- **提高可扩展性**：新增菜品类型时，只需新增一个 `Dish` 子类并实现 `process()`。
- **符合开闭原则**：扩展新功能时尽量不修改原有统计类。
- **结构更清晰**：每种菜品的业务逻辑由自己负责，职责单一。

### 5. 总结
本题重构的关键在于：
- 抽取统一父类或接口；
- 将差异化行为交给子类实现；
- 调用方只依赖抽象类型，通过多态完成统一处理。

这是一种典型的面向对象重构方式，适用于需要根据对象类型执行不同行为的场景。

### 参考代码

```java
// 抽象父类：定义所有菜品的统一行为
public abstract class Dish {
    // 统计类只关心“如何处理菜品”，不关心具体是什么类型
    public abstract void process();
}

// 普通菜品
class RegularDish extends Dish {
    @Override
    public void process() {
        System.out.println("处理普通菜品的统计逻辑");
    }
}

// VIP 菜品
class VIPDish extends Dish {
    @Override
    public void process() {
        System.out.println("处理 VIP 菜品的统计逻辑");
    }
}

// 特价菜品
class DiscountDish extends Dish {
    @Override
    public void process() {
        System.out.println("处理特价菜品的统计逻辑");
    }
}

// 重构后的统计类：不再使用 instanceof
public class DishStatistics {
    public void processAll(Dish[] dishes) {
        for (Dish dish : dishes) {
            dish.process(); // 多态调用
        }
    }

    public static void main(String[] args) {
        Dish[] dishes = {
            new RegularDish(),
            new VIPDish(),
            new DiscountDish()
        };

        DishStatistics statistics = new DishStatistics();
        statistics.processAll(dishes);
    }
}
```
