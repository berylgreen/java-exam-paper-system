# 异常处理 (物流系统)

物流系统需要批量解析外部传入的包裹数据。原有程序在解析过程中，一旦遇到格式错误的数据，就可能抛出未处理的 `RuntimeException`，导致整个程序中断。

请按照以下要求完善程序：
1. 自定义一个受检异常类 `PackageException`，用于表示包裹数据格式错误。
2. 在解析单条包裹数据时，当数据为 `null`、空字符串，或不符合约定格式时，抛出 `PackageException`。
3. 在批量解析方法中使用 `try-catch` 捕获异常；当某条数据解析失败时，输出错误日志并继续处理后续数据，不能因为一条错误数据而终止整个流程。

要求体现 Java 异常处理机制在提升程序健壮性方面的作用。

## 测试数据示例
请在 `Main` 类中使用以下包含错误的数据进行解析演示：
- 数据1：正常格式
- 数据2：错误格式 (触发异常)
- 数据3：正常格式

### 预期输出示例
```text
解析成功：数据1
捕获异常：格式错误，跳过该条数据
解析成功：数据3
全部数据处理完毕
```

---

## 解决方案

本题重点是通过**自定义受检异常**和 **`try-catch` 异常处理结构**，避免单条错误数据导致整个批量解析流程中断。

### 1. 为什么要自定义 `PackageException`
`PackageException` 继承自 `Exception`，属于**受检异常**。这表示：
- 调用者必须显式处理该异常，或继续向上抛出；
- 能够更清晰地表达“包裹数据格式错误”这一业务含义；
- 比直接抛出 `RuntimeException` 更利于程序维护。

### 2. 单条解析方法中抛出异常
在 `parsePackage` 方法中：
- 如果数据为 `null` 或空字符串，说明输入非法；
- 如果数据不符合预设格式（示例中要求包含 `-`），也应判定为格式错误；
- 此时使用 `throw new PackageException(...)` 主动抛出异常。

示例代码：
```java
if (data == null || data.trim().isEmpty()) {
    throw new PackageException("包裹数据不能为空");
}
if (!data.contains("-")) {
    throw new PackageException("包裹数据格式不正确：" + data);
}
```

### 3. 批量解析时捕获异常并继续执行
在 `parseList` 方法中，对每一条数据分别使用 `try-catch` 处理：
- 如果当前数据正常，输出解析成功信息；
- 如果当前数据异常，进入 `catch` 块打印错误日志；
- 异常被捕获后，`for` 循环继续执行，因此不会影响后续数据的解析。

核心代码：
```java
for (String data : dataList) {
    try {
        parsePackage(data);
    } catch (PackageException e) {
        System.err.println("错误日志：" + e.getMessage());
    }
}
```

### 4. 本题体现的异常处理价值
这样设计后，程序具备以下优点：
- **健壮性更强**：单条错误数据不会导致系统崩溃；
- **语义更清晰**：`PackageException` 明确表示业务层面的格式问题；
- **便于维护和扩展**：后续可以在 `catch` 中补充记录日志文件、统计错误数量、发送告警等功能。

因此，本题的关键不是简单“捕获异常”，而是通过合理的异常分类与处理方式，让批量处理程序更加稳定可靠。

### 参考代码

```java
class PackageException extends Exception {
    public PackageException(String message) {
        super(message);
    }
}

public class PackageParser {

    // 解析单条包裹数据
    public void parsePackage(String data) throws PackageException {
        if (data == null || data.trim().isEmpty()) {
            throw new PackageException("包裹数据不能为空");
        }

        // 示例：约定包裹数据中必须包含“-”作为格式标记
        if (!data.contains("-")) {
            throw new PackageException("包裹数据格式不正确：" + data);
        }

        System.out.println("解析成功：" + data);
    }

    // 批量解析包裹数据
    public void parseList(String[] dataList) {
        for (String data : dataList) {
            try {
                parsePackage(data);
            } catch (PackageException e) {
                System.err.println("错误日志：" + e.getMessage());
                // 实际项目中还可以将异常信息写入日志文件或监控系统
            }
        }
    }
}
```
