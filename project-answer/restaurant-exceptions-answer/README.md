# 异常处理 (餐饮系统)

餐饮系统在批量解析外部传入的“菜品”数据时，若某条数据格式不合法，不应因为未捕获异常而导致整个解析流程中断。

请按照以下要求完善程序：
1. 自定义一个受检异常 `DishException`，用于表示菜品数据格式错误。
2. 在解析单条菜品数据时，当数据为空、格式不正确等情况出现时，抛出 `DishException`。
3. 在批量解析过程中使用 `try-catch` 捕获异常：当某条数据解析失败时，输出错误日志并继续处理下一条数据，保证整体流程不中断。


---

## 解决方案

本题核心是使用**受检异常 + try-catch**提高程序健壮性，避免因为单条错误数据导致整个批量处理流程崩溃。

### 1. 为什么要自定义 `DishException`
`DishException` 继承自 `Exception`，属于**受检异常**。这表示：
- 菜品数据格式错误是一种业务层面可预期的问题；
- 调用者必须显式处理该异常，代码可读性和规范性更强。

```java
class DishException extends Exception {
    public DishException(String message) {
        super(message);
    }
}
```

### 2. 为什么在 `parseDish` 中抛出异常
单条数据解析时，如果发现：
- 数据为 `null`
- 数据为空字符串
- 数据不符合预期格式

就应立即抛出 `DishException`，把“错误数据”交给上层统一处理。

```java
public void parseDish(String item) throws DishException {
    if (item == null || item.trim().isEmpty()) {
        throw new DishException("菜品数据为空");
    }
    if (!item.contains(":")) {
        throw new DishException("菜品数据格式错误：" + item);
    }
}
```

### 3. 为什么在批量处理时使用 `try-catch`
批量解析的目标是：**某一条出错，不影响后续数据继续处理**。
因此应把 `try-catch` 放在循环内部，对每一条数据分别处理。

```java
for (String item : data) {
    try {
        parseDish(item);
    } catch (DishException e) {
        System.err.println("[ERROR] 解析失败：" + e.getMessage());
    }
}
```

这样做的效果是：
- 合法数据正常解析；
- 非法数据被记录错误；
- 整个程序不会因为一条异常数据而终止。

### 4. 与原始问题的对应关系
题目中提到的问题是：未捕获的 `RuntimeException` 可能导致系统崩溃。优化后的方案通过：
- 定义明确的业务异常 `DishException`
- 在适当位置抛出异常
- 在批量处理处捕获异常并记录日志

实现了更安全、可维护的异常处理机制。

### 参考代码

```java
class DishException extends Exception {
    public DishException(String message) {
        super(message);
    }
}

public class DishParser {

    public void parseDish(String item) throws DishException {
        if (item == null || item.trim().isEmpty()) {
            throw new DishException("菜品数据为空");
        }

        // 示例：约定菜品数据格式为“菜名:价格”
        if (!item.contains(":")) {
            throw new DishException("菜品数据格式错误：" + item);
        }

        System.out.println("解析成功：" + item);
    }

    public void parseList(String[] data) {
        for (String item : data) {
            try {
                parseDish(item);
            } catch (DishException e) {
                System.err.println("[ERROR] 解析失败：" + e.getMessage());
                // 此处可扩展为写入日志文件或数据库
            }
        }
    }
}
```
