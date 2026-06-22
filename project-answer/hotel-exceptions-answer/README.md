# 异常处理 (酒店系统)

酒店系统需要批量解析外部传入的房间数据。为了避免因单条数据格式错误导致程序整体中断，请按要求完善异常处理逻辑。

**任务要求：**
(1) 自定义一个受检异常类 `RoomException`，用于表示房间数据格式不合法。
(2) 在解析过程中使用 `try-catch` 捕获异常。
(3) 当某条数据解析失败时，输出错误日志并跳过该条数据，继续处理后续数据。

**说明：**
- 受检异常应继承 `Exception`。
- 可将 `null`、空字符串等情况视为非法房间数据。

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

本题考查**自定义受检异常**和**异常捕获后继续执行程序**的处理方式。

(1) `RoomException` 继承自 `Exception`，因此它属于**受检异常**，调用方必须通过 `try-catch` 处理，或继续使用 `throws` 声明抛出。
(2) 将具体的校验逻辑封装到 `parseRoom()` 方法中，如果房间数据为 `null` 或空字符串，就抛出 `RoomException`。
(3) 在 `parseList()` 中遍历数组时，对每条数据分别进行 `try-catch` 处理。这样即使某一条数据异常，也只会记录错误，不会影响后续数据继续解析。
(4) `System.err.println(...)` 用于输出错误日志，体现“记录异常并继续执行”的要求。

这种写法比直接抛出未处理的 `RuntimeException` 更安全，能够提升系统的健壮性，避免因为单条错误数据导致整个批处理流程中断。

### 参考代码

```java
// Main.java
package com.exam.hotel;
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
// Room.java
package com.exam.hotel;

public class Room {
    public String id;
    public String name;
    public double value;
    
    public Room() {}
    
    public Room(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Room{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// DataParser.java
package com.exam.hotel;
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
// CustomException.java
package com.exam.hotel;
public class CustomException extends Exception { public CustomException(String m) { super(m); } }

```

