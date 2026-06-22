# 接口与策略模式 (银行系统)

某银行系统在处理账户业务时，原先通过大量 `switch-case` 根据账户状态或业务类型执行不同逻辑，导致核心代码臃肿，且每次新增业务类型都需要修改原有代码，扩展性较差。

请使用**策略模式**对该设计进行重构，要求如下：
1. 定义统一的策略接口 `AccountStrategy`，用于描述账户业务处理行为；
2. 针对不同业务类型，分别编写实现该接口的具体策略类；
3. 编写上下文类，在运行时动态注入具体策略对象，替代原有 `switch-case` 分支；
4. 给出一个简单示例，演示如何切换不同策略完成不同业务处理。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`张三的账户`应用策略A
- 对`李四的账户`应用策略B

### 预期输出示例
```text
切换到策略A
执行策略A：处理 张三的账户
切换到策略B
执行策略B：处理 李四的账户
```

---

## 解决方案

策略模式的核心思想是：**将不同的业务处理逻辑封装为独立的策略类，并通过统一接口进行管理和调用**。

本题中：

1. **定义策略接口**  
   `AccountStrategy` 规定了所有账户业务策略都必须实现 `execute()` 方法，从而保证调用方式一致。

2. **实现具体策略类**  
   例如 `NormalStrategy` 和 `UrgentStrategy` 分别表示不同业务分支的处理逻辑。以后如果新增一种业务类型，只需新增一个策略类，而不必修改原有核心处理代码。

3. **设计上下文类**  
   `AccountProcessor` 负责持有当前策略对象，并在 `process()` 方法中调用策略执行具体逻辑。这样就将“选择哪种业务逻辑”和“如何执行业务逻辑”分离开来。

4. **替代 `switch-case` 的优势**  
   使用策略模式后，可以减少大量条件分支判断，使代码结构更清晰，也更符合“开闭原则”——对扩展开放、对修改关闭。

如果原先代码类似下面这样：

```java
switch (type) {
    case "NORMAL":
        System.out.println("普通账户业务处理");
        break;
    case "URGENT":
        System.out.println("紧急账户业务处理");
        break;
}
```

那么重构后，不再依赖集中式分支判断，而是通过动态注入不同的策略对象来完成对应处理，提高了系统的可维护性与可扩展性。

### 参考代码

```java
// 策略接口：定义统一的业务处理方法
interface AccountStrategy {
    void execute();
}

// 具体策略1：普通账户业务处理
class NormalStrategy implements AccountStrategy {
    @Override
    public void execute() {
        System.out.println("普通账户业务处理");
    }
}

// 具体策略2：紧急账户业务处理
class UrgentStrategy implements AccountStrategy {
    @Override
    public void execute() {
        System.out.println("紧急账户业务处理");
    }
}

// 上下文类：持有策略对象，并在运行时调用具体策略
class AccountProcessor {
    private AccountStrategy strategy;

    public void setStrategy(AccountStrategy strategy) {
        this.strategy = strategy;
    }

    public void process() {
        if (strategy == null) {
            System.out.println("未设置账户处理策略");
            return;
        }
        strategy.execute();
    }
}

// 测试类
public class Main {
    public static void main(String[] args) {
        AccountProcessor processor = new AccountProcessor();

        // 使用普通账户策略
        processor.setStrategy(new NormalStrategy());
        processor.process();

        // 切换为紧急账户策略
        processor.setStrategy(new UrgentStrategy());
        processor.process();
    }
}
```
