# 接口与策略模式 (餐饮系统)

餐饮系统在处理不同菜品业务时，原先将所有分支逻辑集中写在冗长的 `switch-case` 中，导致核心类职责过重，且每次新增业务类型都需要直接修改原有代码。

**任务要求：**
1. 使用策略模式重构该场景，定义一个统一的 `DishStrategy` 策略接口。
2. 针对不同业务类型，分别编写实现该接口的具体策略类。
3. 编写上下文类，在运行时动态注入具体策略对象，替代原有的 `switch-case` 分支处理逻辑。
4. 给出一个简单的测试示例，展示如何切换不同策略完成不同业务处理。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`宫保鸡丁`应用策略A
- 对`鱼香肉丝`应用策略B

### 预期输出示例
```text
切换到策略A
执行策略A：处理 宫保鸡丁
切换到策略B
执行策略B：处理 鱼香肉丝
```

---

## 解决方案

```java
// 原有问题：
// 如果使用 switch-case，代码可能类似下面这种形式：
/*
public void process(String type, String dishName) {
    switch (type) {
        case "DINE_IN":
            System.out.println("堂食菜品处理：" + dishName);
            break;
        case "TAKE_AWAY":
            System.out.println("打包菜品处理：" + dishName);
            break;
        case "URGENT":
            System.out.println("加急菜品处理：" + dishName);
            break;
        default:
            throw new IllegalArgumentException("不支持的业务类型");
    }
}
*/
```

策略模式的核心思想是：**将不同的业务处理算法分别封装到独立的策略类中，并通过统一接口对外提供能力**。

本题中各部分职责如下：

1. `DishStrategy`：策略接口，定义统一的处理方法 `process(String dishName)`。
2. `DineInStrategy`、`TakeAwayStrategy`、`UrgentStrategy`：具体策略类，分别实现不同业务逻辑。
3. `DishContext`：上下文类，负责持有当前策略对象，并在执行时调用对应策略。
4. `Main`：演示如何在运行时动态切换策略。

这样重构后的优点有：

- **消除冗长条件分支**：避免在核心类中堆积大量 `switch-case`。
- **符合开闭原则**：新增业务类型时，只需增加新的策略类，通常不需要修改已有核心处理代码。
- **提高可维护性和可扩展性**：每种业务逻辑互相独立，便于测试与复用。
- **运行时可灵活切换**：可以根据实际业务动态注入不同策略。

因此，使用策略模式可以更优雅地替代原有的分支判断结构，使系统设计更加清晰。

### 参考代码

```java
// 策略接口
interface DishStrategy {
    void process(String dishName);
}

// 具体策略：堂食处理
class DineInStrategy implements DishStrategy {
    @Override
    public void process(String dishName) {
        System.out.println("堂食菜品处理：" + dishName);
    }
}

// 具体策略：打包处理
class TakeAwayStrategy implements DishStrategy {
    @Override
    public void process(String dishName) {
        System.out.println("打包菜品处理：" + dishName);
    }
}

// 具体策略：加急处理
class UrgentStrategy implements DishStrategy {
    @Override
    public void process(String dishName) {
        System.out.println("加急菜品处理：" + dishName);
    }
}

// 上下文类
class DishContext {
    private DishStrategy strategy;

    public void setStrategy(DishStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeStrategy(String dishName) {
        if (strategy == null) {
            throw new IllegalStateException("请先设置菜品处理策略");
        }
        strategy.process(dishName);
    }
}

// 测试类
public class Main {
    public static void main(String[] args) {
        DishContext context = new DishContext();

        context.setStrategy(new DineInStrategy());
        context.executeStrategy("宫保鸡丁");

        context.setStrategy(new TakeAwayStrategy());
        context.executeStrategy("鱼香肉丝");

        context.setStrategy(new UrgentStrategy());
        context.executeStrategy("红烧排骨");
    }
}
```
