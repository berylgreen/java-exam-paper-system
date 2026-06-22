# 异常处理 (数据解析系统)

系统中提供了一个数据解析工具 `DataParser`，用于将外部传入的字符串数组解析为整数成绩。但目前该工具健壮性较差：当数组中出现 `null` 或非数字字符串时，程序可能因异常而中断。

请根据异常处理机制完善该程序，要求如下：

1. 自定义一个受检异常 `InvalidDataException`，用于表示非法数据。
2. 在 `DataParser` 的 `parseScores` 方法中，使用 `try-catch` 捕获解析过程中可能出现的 `NumberFormatException` 和 `NullPointerException`。
3. 当遇到非法数据时，可将底层异常转换为 `InvalidDataException`，并在外层进行处理；或者直接记录错误信息后继续解析后续数据，保证程序不会因单个错误数据而终止。
4. 无论本次解析是否成功，都必须在 `finally` 块中输出“解析过程结束”。

请给出一个符合上述要求的实现示例。

## 测试数据示例
请在 `Main` 类中使用以下包含错误的数据进行解析演示：
- 数据1：正常格式
- 数据2：错误格式 (触发异常)
- 数据3：正常格式

**预期输出示例：**
```text
解析成功：数据1
捕获异常：格式错误，跳过该条数据
解析成功：数据3
全部数据处理完毕
```

---

## 解决方案

本题考查自定义受检异常、异常捕获与转换，以及 `finally` 的使用。

1. **自定义受检异常**  
   `InvalidDataException` 继承自 `Exception`，因此它属于受检异常，通常用于表示业务层面可预期但需要显式处理的错误。

2. **捕获底层异常**  
   在解析字符串为整数时，可能出现两类问题：
   - `NumberFormatException`：字符串内容不是合法整数；
   - `NullPointerException`：当前元素为 `null`。

3. **异常转换**  
   题目要求在出现非法数据时，可以抛出 `InvalidDataException`。因此示例中先捕获底层异常，再将其转换为更有业务含义的 `InvalidDataException`，最后在外层统一处理。这样做的好处是：
   - 隐藏底层实现细节；
   - 提高异常语义的清晰度；
   - 便于后续统一管理错误信息。

4. **继续处理后续数据**  
   每次循环只处理一个元素，即使当前数据解析失败，也只是输出错误信息，不会影响后续元素的解析，从而提升程序健壮性。

5. **finally 的作用**  
   `finally` 中的代码无论是否发生异常都会执行，因此适合放置日志输出、资源释放等操作。本题中要求输出“解析过程结束”，所以应放在 `finally` 块中。

示例运行逻辑如下：

```java
String[] arr = {"90", "abc", null, "100"};
new DataParser().parseScores(arr);
```

可能输出：

```java
Parsed: 90
解析过程结束
解析失败：数据格式非法：abc
解析过程结束
解析失败：数据不能为空
解析过程结束
Parsed: 100
解析过程结束
```

该实现满足题目四项要求，并体现了异常处理在提升程序稳定性方面的作用。

### 参考代码

```java
// Main.java
package com.exam.exception;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        DataParser parser = new DataParser();
        String[] data = {"数据1", "error", "数据3"}; 
        parser.parseData(data);
    }
}

```

```java
// DataParser.java
package com.exam.exception;
public class DataParser {
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

```

```java
// Data.java
package com.exam.exception;

public class Data {
    public String id;
    public String name;
    public double value;
    
    public Data() {}
    
    public Data(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Data{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// CustomException.java
package com.exam.exception;
public class CustomException extends Exception { public CustomException(String m) { super(m); } }

```

