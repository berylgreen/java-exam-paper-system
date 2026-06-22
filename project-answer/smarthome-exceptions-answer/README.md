# 异常处理 (智能家居)

智能家居系统在批量解析外部设备数据时，如果某条数据格式不合法，不应因未处理异常而导致整个程序中断。

**任务要求：**  
(1) 自定义一个受检异常 `DeviceException`，用于表示设备数据格式错误。  
(2) 在解析过程中使用 `try-catch` 结构处理异常。  
(3) 当某条数据解析失败时，输出错误日志并跳过该条数据，继续处理后续数据。

请编写相应的 Java 代码实现上述功能。

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

本题考查 **自定义受检异常** 和 **异常处理机制** 的基本应用。

### 1. 为什么要自定义 `DeviceException`
题目要求使用受检异常来表示设备数据格式错误，因此应继承 `Exception`，而不是继承 `RuntimeException`。这样可以明确告诉调用者：该异常是业务上可预期、需要处理的异常。

```java
class DeviceException extends Exception {
    public DeviceException(String message) {
        super(message);
    }
}
```

### 2. 为什么要使用 `try-catch`
批量处理数据时，如果某一条数据有问题，不能让整个流程终止。因此应将“单条数据的解析”放在 `try` 块中，一旦抛出 `DeviceException`，就在 `catch` 中处理错误并继续下一条数据。

```java
for (String data : dataList) {
    try {
        parseDevice(data);
        System.out.println("解析成功：" + data);
    } catch (DeviceException e) {
        System.err.println("设备数据解析失败：" + e.getMessage());
    }
}
```

这样做的好处是：
- 单条错误不会影响整体批量处理；
- 程序具有更好的健壮性；
- 错误信息可以被记录，便于后续排查。

### 3. 解析逻辑设计
`parseDevice` 方法负责检查设备数据是否合法，例如：
- 数据不能为 `null`；
- 数据不能是空字符串；
- 数据必须包含规定的分隔符。

如果不满足条件，就抛出 `DeviceException`：

```java
private void parseDevice(String data) throws DeviceException {
    if (data == null || data.trim().isEmpty()) {
        throw new DeviceException("设备数据为空或格式非法");
    }
    if (!data.contains(":")) {
        throw new DeviceException("设备数据缺少必要分隔符: " + data);
    }
}
```

### 4. 原答案中的问题
原代码中存在以下问题：
- 自定义异常类命名不合理，`RuntimeExceptionException` 容易造成混淆；
- 题目要求的是 `DeviceException`；
- 虽然继承了 `Exception`，但没有体现“设备数据解析”的业务语义；
- 解析逻辑和异常含义不够清晰。

### 5. 总结
本题的核心是：
- 使用 `Exception` 自定义受检异常；
- 在批量处理时对每条数据分别进行异常捕获；
- 出现异常时记录错误并继续执行后续任务。

这是一种常见的容错处理方式，适用于日志解析、设备数据接收、文件批量导入等场景。

### 参考代码

```java
// Main.java
package com.exam.smarthome;
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
package com.exam.smarthome;
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
// Device.java
package com.exam.smarthome;

public class Device {
    public String id;
    public String name;
    public double value;
    
    public Device() {}
    
    public Device(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Device{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// CustomException.java
package com.exam.smarthome;
public class CustomException extends Exception { public CustomException(String m) { super(m); } }

```

