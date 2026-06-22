# 接口与策略模式 (物流系统)

物流系统在处理包裹业务时，原有代码使用了大量 `switch-case` 来区分不同处理类型，导致代码难以维护，并且每次新增业务类型都需要修改核心处理类。

请使用**策略模式**对该设计进行优化，要求如下：
1. 定义一个 `PackageStrategy` 接口，用于描述包裹处理策略的统一行为。
2. 针对不同的业务类型，分别编写具体策略类并实现该接口。
3. 编写一个上下文类 `PackageProcessor`，通过动态注入策略对象来完成包裹处理。
4. 给出示例代码，展示如何分别处理普通包裹和加急包裹。

要求体现：将原先依赖 `switch-case` 的分支处理方式，改为基于策略对象的可扩展设计。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`电子产品包裹`应用策略A
- 对`书籍包裹`应用策略B

### 预期输出示例
```text
切换到策略A
执行策略A：处理 电子产品包裹
切换到策略B
执行策略B：处理 书籍包裹
```

---

## 解决方案

```java
// 原来的做法通常类似：
class OldPackageProcessor {
    public void process(String type) {
        switch (type) {
            case "normal":
                System.out.println("普通包裹处理流程");
                break;
            case "urgent":
                System.out.println("加急包裹优先处理流程");
                break;
            default:
                throw new IllegalArgumentException("未知包裹类型");
        }
    }
}
```

上述写法的问题是：
1. 处理逻辑集中在一个类中，分支过多时可读性差。
2. 每新增一种业务类型，都要修改原有核心类，违反**开闭原则**。
3. 不同业务逻辑难以独立维护、测试和复用。

使用策略模式后：
1. `PackageStrategy` 接口定义统一行为，体现“面向接口编程”。
2. `NormalPackageStrategy`、`UrgentPackageStrategy` 等具体策略类分别封装各自业务逻辑。
3. `PackageProcessor` 作为上下文类，不再关心具体是哪一种包裹，只负责调用当前注入的策略。
4. 若以后增加“冷链包裹”“国际包裹”等类型，只需新增对应策略类，而无需修改 `PackageProcessor` 的核心代码。

这种设计将“选择哪种处理方式”和“具体如何处理”分离开来，能够有效替代冗长的 `switch-case`，提高系统的可扩展性与可维护性。

### 参考代码

```java
// 策略接口：定义统一的处理方法
interface PackageStrategy {
    void process();
}

// 具体策略：普通包裹处理
class NormalPackageStrategy implements PackageStrategy {
    @Override
    public void process() {
        System.out.println("普通包裹处理流程");
    }
}

// 具体策略：加急包裹处理
class UrgentPackageStrategy implements PackageStrategy {
    @Override
    public void process() {
        System.out.println("加急包裹优先处理流程");
    }
}

// 上下文类：持有策略对象，并调用策略完成处理
class PackageProcessor {
    private PackageStrategy strategy;

    public void setStrategy(PackageStrategy strategy) {
        this.strategy = strategy;
    }

    public void processPackage() {
        if (strategy == null) {
            throw new IllegalStateException("未设置包裹处理策略");
        }
        strategy.process();
    }
}

// 测试示例
public class Main {
    public static void main(String[] args) {
        PackageProcessor processor = new PackageProcessor();

        // 处理普通包裹
        processor.setStrategy(new NormalPackageStrategy());
        processor.processPackage();

        // 处理加急包裹
        processor.setStrategy(new UrgentPackageStrategy());
        processor.processPackage();
    }
}
```
