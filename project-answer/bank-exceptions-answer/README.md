# 异常处理 (银行系统)

银行系统需要批量解析外部传入的账户数据。若某条数据格式不合法，程序不应因未捕获的运行时异常而中断，而应跳过该条错误数据并继续处理后续数据。

**任务要求：**  
(1) 自定义一个受检异常 `AccountException`，用于表示账户数据格式错误。  
(2) 在解析过程中使用 `try-catch` 捕获异常，避免程序因单条错误数据而崩溃。  
(3) 当发生异常时，输出错误日志并继续解析下一条数据，保证整体流程不中断。

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

```java
class AccountException extends Exception {
    public AccountException(String message) {
        super(message);
    }
}
```
以上代码定义了一个受检异常 `AccountException`。由于它继承自 `Exception`，因此属于受检异常，调用相关方法时必须显式处理或继续抛出。

```java
private void parse(String item) throws AccountException {
    if (item == null || item.trim().isEmpty()) {
        throw new AccountException("账户数据格式错误");
    }
}
```
`parse` 方法负责校验单条账户数据。当数据为 `null` 或空字符串时，说明格式不合法，此时抛出自定义异常，提示调用者该数据存在问题。

```java
public void parseList(String[] data) {
    for (String item : data) {
        try {
            parse(item);
            System.out.println("Parsed: " + item);
        } catch (AccountException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
```
在批量处理方法 `parseList` 中，使用 `for` 循环逐条解析数据，并在每次循环内通过 `try-catch` 捕获 `AccountException`。

这样做有两个关键作用：  
(1) **避免程序整体中断**：某一条数据出错时，只处理当前异常，不影响后续数据。  
(2) **提高系统健壮性**：通过输出错误信息，可以定位问题数据，便于后续排查和修复。

本题的核心是理解：
- 自定义受检异常的定义方式；
- 使用 `throws` 声明异常；
- 使用 `try-catch` 在批量处理中实现“局部出错、整体继续”的容错机制。

### 参考代码

```java
// Main.java
package com.exam.bank;
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
package com.exam.bank;
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
// Account.java
package com.exam.bank;

public class Account {
    public String id;
    public String name;
    public double value;
    
    public Account() {}
    
    public Account(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Account{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// CustomException.java
package com.exam.bank;
public class CustomException extends Exception { public CustomException(String m) { super(m); } }

```

