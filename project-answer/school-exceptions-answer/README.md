# 异常处理 (教务系统)

教务系统在批量解析外部学生数据时，如果某条数据格式不正确，不能因为未捕获异常而导致整个系统中断。

请按照以下要求完善程序：
1. 自定义一个受检异常 `StudentException`，用于表示学生数据格式错误。
2. 在解析单条学生数据时，当数据为空、格式不合法等情况出现时，主动抛出 `StudentException`。
3. 在批量解析过程中使用 `try-catch` 捕获异常，输出错误日志后继续处理下一条数据，保证整体流程不中断。

要求体现“单条数据出错不影响其余数据处理”的异常处理思想。

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

```java
// 处理思路：
// 1. 自定义 StudentException，并继承 Exception，说明它是受检异常。
// 2. 在解析单条数据的方法 parseStudent 中，发现格式问题时使用 throw 主动抛出异常。
// 3. 在批量处理方法 parseList 中，使用 try-catch 捕获 StudentException。
// 4. 某条数据解析失败时，只记录错误信息，不结束整个 for 循环，因此后续数据仍会继续处理。
```

本题重点是区分“受检异常”和“运行时异常”的处理方式：
- `StudentException` 继承 `Exception`，属于受检异常，调用者必须显式处理或继续抛出。
- 通过将单条数据解析封装到 `parseStudent` 方法中，可以让异常职责更清晰。
- 在循环内部捕获异常，而不是在循环外统一捕获，这样才能保证某一条数据出错时，不会影响后续数据的解析。

这种写法能够提高系统健壮性，避免因个别脏数据导致整个批处理任务崩溃。

### 参考代码

```java
package com.exam.school;
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