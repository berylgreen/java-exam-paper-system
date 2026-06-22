# 接口与策略模式 (教务系统)

教务系统在处理学生的不同业务类型时，原有实现将所有分支逻辑集中写在 `switch-case` 中，导致核心类职责过重，且每次新增一种业务类型都必须修改已有代码，扩展性较差。

**任务要求：**
1. 使用策略模式重构该业务处理流程，定义统一的策略接口 `StudentStrategy`。
2. 针对不同业务类型，分别编写实现 `StudentStrategy` 接口的具体策略类。
3. 编写上下文类，在运行时通过传入不同的策略对象完成处理，从而替代原有的 `switch-case` 分支结构。
4. 给出一个简单的使用示例，展示如何动态切换不同策略。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`高等数学`应用策略A
- 对`大学物理`应用策略B

### 预期输出示例
```text
切换到策略A
执行策略A：处理 高等数学
切换到策略B
执行策略B：处理 大学物理
```

---

## 解决方案

策略模式的核心思想是：**将不同的处理算法封装为独立的策略类，并通过统一接口对外提供能力**。这样可以把原本集中在 `switch-case` 中的分支逻辑拆分出去。

### 本题重构后的设计说明
1. **定义策略接口 `StudentStrategy`**  
   该接口规定所有学生业务处理方式都必须实现 `execute()` 方法，从而保证调用方式统一。

2. **定义具体策略类**  
   例如：
   - `NormalStudentStrategy`：处理普通业务；
   - `UrgentStudentStrategy`：处理紧急业务。  
   如果以后新增“毕业审核业务”或“学籍异动业务”，只需新增对应策略类即可。

3. **定义上下文类 `StudentProcessor`**  
   上下文类负责持有某个策略对象，并在 `process()` 方法中调用该策略，而不再关心具体是哪一种业务实现。

4. **动态切换策略**  
   通过 `setStrategy()` 方法，可以在运行时灵活替换处理方式，而不需要修改上下文类内部代码。

### 相比 `switch-case` 的优点
- **符合开闭原则**：新增业务时，扩展新策略类即可，不必修改原有核心处理类。
- **降低耦合度**：上下文类只依赖策略接口，不依赖具体业务实现。
- **代码更清晰**：每种业务逻辑独立封装，便于维护和测试。

### 示例调用流程
```java
StudentProcessor processor = new StudentProcessor();
processor.setStrategy(new NormalStudentStrategy());
processor.process();
```
上述代码表示：给 `StudentProcessor` 注入“普通业务处理策略”，随后调用统一入口 `process()` 完成处理。

因此，该实现已经满足题目中“定义策略接口、编写具体策略类、在上下文中动态注入策略以替换 `switch-case`”的要求。

### 参考代码

```java
// 策略接口
interface StudentStrategy {
    void execute();
}

// 具体策略：普通业务处理
class NormalStudentStrategy implements StudentStrategy {
    @Override
    public void execute() {
        System.out.println("执行普通学生业务处理");
    }
}

// 具体策略：紧急业务处理
class UrgentStudentStrategy implements StudentStrategy {
    @Override
    public void execute() {
        System.out.println("执行紧急学生业务处理");
    }
}

// 上下文类
class StudentProcessor {
    private StudentStrategy strategy;

    public void setStrategy(StudentStrategy strategy) {
        this.strategy = strategy;
    }

    public void process() {
        if (strategy == null) {
            throw new IllegalStateException("未设置学生业务处理策略");
        }
        strategy.execute();
    }
}

// 测试类
public class Main {
    public static void main(String[] args) {
        StudentProcessor processor = new StudentProcessor();

        processor.setStrategy(new NormalStudentStrategy());
        processor.process();

        processor.setStrategy(new UrgentStudentStrategy());
        processor.process();
    }
}
```
