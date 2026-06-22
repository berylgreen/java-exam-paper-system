# 接口与策略模式 (医疗系统)

某医疗系统在处理病患业务时，原先通过冗长的 `switch-case` 来区分不同的处理类型，导致核心代码难以维护，并且每新增一种业务类型都需要修改原有逻辑。

请使用**策略模式**对该场景进行重构，要求如下：
1. 定义一个统一的策略接口 `PatientStrategy`，用于描述病患业务的处理行为；
2. 针对不同业务类型，分别编写实现该接口的具体策略类；
3. 编写上下文类，在运行时动态注入不同策略对象，从而替代原有的 `switch-case` 分支判断；
4. 给出示例代码，演示普通病患与紧急病患两种业务的处理方式。


---

## 解决方案

```java
// 策略模式的核心结构：
// 1. Strategy（策略接口）：定义统一的业务处理方法
// 2. ConcreteStrategy（具体策略类）：封装不同的业务实现
// 3. Context（上下文类）：持有策略对象，并在运行时调用对应策略
```

本题中，原有系统使用 `switch-case` 根据病患业务类型执行不同逻辑，这样会带来以下问题：
1. 核心类职责过重，既负责调度又负责实现具体业务；
2. 扩展性差，每增加一种病患处理方式都要修改原有代码；
3. 不符合“开闭原则”，即对扩展开放、对修改关闭。

使用策略模式后：
- `PatientStrategy` 作为统一接口，约定所有病患处理方式都必须实现 `process()` 方法；
- `NormalPatientStrategy` 和 `UrgentPatientStrategy` 分别封装不同业务逻辑；
- `PatientProcessor` 只负责持有并调用策略，不关心具体实现细节。

这样做的优点是：
1. 去除了冗长的条件分支；
2. 新增业务类型时，只需增加新的策略类即可；
3. 提高了代码的可维护性、可扩展性和可读性。

例如，若后续需要增加“VIP病患处理策略”，只需新增一个实现 `PatientStrategy` 的类，而不必修改 `PatientProcessor` 的核心代码。

### 参考代码

```java
// 1. 定义策略接口
interface PatientStrategy {
    void process();
}

// 2. 具体策略：普通病患处理
class NormalPatientStrategy implements PatientStrategy {
    @Override
    public void process() {
        System.out.println("普通病患：按常规流程处理");
    }
}

// 3. 具体策略：紧急病患处理
class UrgentPatientStrategy implements PatientStrategy {
    @Override
    public void process() {
        System.out.println("紧急病患：优先安排抢救与检查");
    }
}

// 4. 上下文类
class PatientProcessor {
    private PatientStrategy strategy;

    public void setStrategy(PatientStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeStrategy() {
        if (strategy == null) {
            throw new IllegalStateException("未设置病患处理策略");
        }
        strategy.process();
    }
}

// 5. 测试示例
public class Main {
    public static void main(String[] args) {
        PatientProcessor processor = new PatientProcessor();

        // 处理普通病患
        processor.setStrategy(new NormalPatientStrategy());
        processor.executeStrategy();

        // 处理紧急病患
        processor.setStrategy(new UrgentPatientStrategy());
        processor.executeStrategy();
    }
}
```
