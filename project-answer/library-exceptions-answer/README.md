# 异常处理 (图书馆系统)

图书馆系统在批量解析外部导入的图书数据时，若某条数据格式不正确，不应因为未处理的异常而导致整个系统中断。

**任务要求：**
1. 自定义一个受检异常 `BookException`，用于表示图书数据格式错误。
2. 在解析过程中使用 `try-catch` 捕获异常。
3. 当某条数据解析失败时，输出错误信息并继续处理下一条数据，保证批量解析流程不中断。

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

本题考查**自定义受检异常**和**异常捕获处理**。

1. `BookException` 继承 `Exception`，因此它是一个**受检异常**，调用相关方法时必须显式处理或继续抛出。
2. 将单条数据的解析逻辑封装到 `parseBook` 方法中，当数据为空或格式非法时，通过 `throw new BookException(...)` 主动抛出异常。
3. 在 `parseList` 方法中使用 `for` 循环逐条处理数据，并在每次循环中用 `try-catch` 捕获 `BookException`。
4. 这样即使某一条数据出错，也只会记录当前错误，不会影响后续数据的解析，从而提高系统的健壮性。

这种处理方式适用于批量导入、日志分析、文件解析等场景，能够避免“单条错误导致整体失败”的问题。

### 参考代码

```java
// Main.java
package com.exam.library;
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
package com.exam.library;
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
// Book.java
package com.exam.library;

public class Book {
    public String id;
    public String name;
    public double value;
    
    public Book() {}
    
    public Book(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Book{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// CustomException.java
package com.exam.library;
public class CustomException extends Exception { public CustomException(String m) { super(m); } }

```

