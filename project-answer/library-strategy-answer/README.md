# 接口与策略模式 (图书馆系统)

某图书馆管理系统在处理图书业务时，原本通过冗长的 `switch-case` 判断不同业务类型（如普通借阅、加急处理、馆内阅览等）。这种做法会导致核心代码难以维护，并且每次新增一种业务类型时，都需要修改原有核心类，扩展性较差。

**任务要求：**
1. 使用策略模式对原有实现进行重构，定义统一的 `BookStrategy` 策略接口。
2. 针对不同的图书业务类型，分别创建实现该接口的具体策略类。
3. 定义上下文类，在运行时动态注入具体策略对象，替代原有的 `switch-case` 分支逻辑。
4. 给出一个简单示例，展示如何切换不同策略来处理图书业务。


---

## 解决方案

```java
// 策略模式的核心思想：
// 将“不同业务处理方式”封装为独立的策略类，
// 让上下文类在运行时选择并调用具体策略，
// 从而避免大量的 if-else 或 switch-case。
```

本题中：

1. **`BookStrategy` 接口**
   - 统一定义图书业务处理方法 `handle(String bookName)`。
   - 所有具体业务策略都必须实现该接口。

2. **具体策略类**
   - `NormalBorrowStrategy`：处理普通借阅。
   - `UrgentBorrowStrategy`：处理加急借阅。
   - `ReadingRoomStrategy`：处理馆内阅览。
   - 每种业务逻辑独立封装，便于维护和扩展。

3. **上下文类 `BookContext`**
   - 持有一个 `BookStrategy` 类型的引用。
   - 通过 `setStrategy()` 方法在运行时动态切换策略。
   - `process()` 方法不再关心具体是哪种业务，只负责调用当前策略。

4. **相对于 `switch-case` 的优势**
   - **符合开闭原则**：新增业务类型时，只需增加新的策略类，不需要修改原有核心处理代码。
   - **降低耦合度**：业务逻辑与上下文分离。
   - **提高可维护性**：每个策略职责单一，结构更清晰。

因此，使用策略模式后，系统能够更灵活地应对图书业务类型的变化，避免因频繁修改核心类而带来的维护问题。

### 参考代码

```java
// 1. 定义策略接口
interface BookStrategy {
    void handle(String bookName);
}

// 2. 具体策略：普通借阅处理
class NormalBorrowStrategy implements BookStrategy {
    @Override
    public void handle(String bookName) {
        System.out.println("普通借阅处理：" + bookName);
    }
}

// 3. 具体策略：加急处理
class UrgentBorrowStrategy implements BookStrategy {
    @Override
    public void handle(String bookName) {
        System.out.println("加急借阅处理：" + bookName);
    }
}

// 4. 具体策略：馆内阅览处理
class ReadingRoomStrategy implements BookStrategy {
    @Override
    public void handle(String bookName) {
        System.out.println("馆内阅览处理：" + bookName);
    }
}

// 5. 上下文类
class BookContext {
    private BookStrategy strategy;

    public void setStrategy(BookStrategy strategy) {
        this.strategy = strategy;
    }

    public void process(String bookName) {
        if (strategy == null) {
            throw new IllegalStateException("请先设置图书处理策略");
        }
        strategy.handle(bookName);
    }
}

// 6. 测试类
public class Main {
    public static void main(String[] args) {
        BookContext context = new BookContext();

        context.setStrategy(new NormalBorrowStrategy());
        context.process("Java 程序设计");

        context.setStrategy(new UrgentBorrowStrategy());
        context.process("数据结构");

        context.setStrategy(new ReadingRoomStrategy());
        context.process("操作系统");
    }
}
```
