# 异常处理 (医疗系统)

医疗系统需要批量解析外部传入的病患数据。由于外部数据格式可能不规范，如果在解析过程中直接抛出未处理的运行时异常，可能导致整个批处理流程中断。

请按要求完善程序：
1. 自定义一个受检异常 `PatientException`，用于表示病患数据格式错误。
2. 在解析单条病患数据时，当数据为空、为空字符串或格式不合法时，抛出 `PatientException`。
3. 在批量解析时使用 `try-catch` 捕获异常：当某条数据解析失败时，输出错误日志并继续处理下一条数据，不能因为单条错误数据而终止整个流程。

要求体现出“局部出错、整体不中断”的异常处理思想。

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

本题考查 **自定义受检异常** 和 **异常捕获后继续执行后续流程** 的处理方式。

### 1. 为什么要自定义 `PatientException`
`PatientException` 继承自 `Exception`，因此它属于**受检异常**。这表示：
- 调用者必须显式处理该异常；
- 更适合用于表示“可以预期的业务错误”，例如病患数据格式不正确。

```java
class PatientException extends Exception {
    public PatientException(String message) {
        super(message);
    }
}
```

### 2. 为什么在 `parsePatient` 中抛出异常
单条数据的解析逻辑应负责发现问题并报告问题，因此当数据为空或格式非法时，应主动抛出异常。

```java
if (data == null || data.trim().isEmpty()) {
    throw new PatientException("病患数据为空，无法解析");
}
```

这样可以将“错误检测”和“错误处理”分离，使程序结构更清晰。

### 3. 为什么在批量处理中使用 `try-catch`
批量处理的核心目标是：即使某一条数据出错，也不能影响其他数据继续解析。
因此应在循环内部进行捕获：

```java
for (String data : dataList) {
    try {
        parsePatient(data);
    } catch (PatientException e) {
        System.err.println("解析失败：" + e.getMessage());
    }
}
```

这样当某一条数据异常时：
- 当前数据会被记录为失败；
- 程序不会崩溃；
- 后续数据仍然会继续处理。

### 4. 原题答案中存在的问题
原答案中主要有以下不足：
- 异常类名 `RuntimeExceptionException` 含义不清晰，不符合题意；
- 题目要求自定义的是 `PatientException`；
- 解析逻辑与业务场景“病患数据”关联不够明确；
- 没有清楚体现“单条失败不影响整体”的设计意图。

### 5. 本题体现的异常处理思想
本题的关键不是“完全消除错误”，而是通过合理的异常机制实现：
- 发现错误；
- 记录错误；
- 控制错误影响范围；
- 保证整体流程稳定运行。

这正是实际系统中常见的健壮性处理方式。

### 参考代码

```java
package com.exam.hospital;
class CustomException extends Exception {
    public CustomException(String msg) { super(msg); }
}
class DataParser {
    public void parseData(String[] data) {
        for (String item : data) {
            try {
                if ("error".equals(item)) throw new CustomException("格式错误");
                System.out.println("解析成功：" + item);
            } catch (CustomException e) {
                System.out.println("捕获异常：" + e.getMessage() + "，跳过该条数据");
            }
        }
        System.out.println("全部数据处理完毕");
    }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        DataParser parser = new DataParser();
        String[] data = {"数据1", "error", "数据3"}; 
        parser.parseData(data);
    }
}

```