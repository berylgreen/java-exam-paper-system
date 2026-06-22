# 多态与继承 (酒店系统)

酒店系统中原有一个体积较大的统计类，其中使用了大量 `if (obj instanceof 某种房间类型) ... else if ...` 的方式，对不同类型的房间对象分别执行处理逻辑。这种写法可读性较差，也不利于后续扩展。

**请使用面向对象中的多态思想对其进行重构，要求如下：**  
(1) 定义一个通用的 `Room` 抽象类或接口，并声明统一的处理方法；  
(2) 为不同类型的房间创建对应的子类，并在子类中实现各自的处理逻辑；  
(3) 编写统计类，在不使用 `instanceof` 或类型强制转换判断的前提下，统一处理所有房间对象。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础客房对象：`总统套房`
- 创建高级客房对象：`豪华大床房`
- 使用统一方法遍历调用处理逻辑

**预期输出示例：**
```text
统一处理基础客房：总统套房
统一处理高级客房：豪华大床房
```

---

## 解决方案

本题的核心是用**多态**替代大量的 `instanceof` 分支判断。

### 重构思路  
(1) **抽象父类 `Room`**
   将所有房间共有的“处理行为”抽象为 `process()` 方法。  
(2) **子类分别实现**
   不同房间类型如 `RegularRoom`、`VIPRoom`、`SuiteRoom` 在各自类中重写 `process()`，实现自己的业务逻辑。  
(3) **调用方统一处理**
   统计类只需要面向 `Room` 类型编程，遍历房间数组时直接调用 `room.process()`，无需再关心对象的实际类型。

### 这样做的优点
- **消除类型判断**：不再需要 `if...else if...` 或 `instanceof`；
- **提高可维护性**：每种房间的逻辑放在自己的类中，职责更清晰；
- **符合开闭原则**：如果以后新增 `DeluxeRoom`，只需新增一个子类并实现 `process()`，统计类代码无需修改；
- **体现面向对象思想**：把“做什么”交给对象自己决定，而不是由外部统一判断类型后再处理。

### 对比说明
原来的写法通常类似：

```java
if (obj instanceof RegularRoom) {
    // 处理普通房间
} else if (obj instanceof VIPRoom) {
    // 处理 VIP 房间
} else if (obj instanceof SuiteRoom) {
    // 处理套房
}
```

这种方式每增加一种房间类型，就要修改原有统计类。

重构后：

```java
room.process();
```

同一条语句即可完成不同类型对象的处理，这正是多态的典型应用。

### 参考代码

```java
// Main.java
package com.exam.hotel;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        RoomProcessor[] processors = { new RegularRoom("总统套房"), new VIPRoom("豪华大床房") };
        for (RoomProcessor p : processors) { p.process(); }
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
// RegularRoom.java
package com.exam.hotel;
public class RegularRoom implements RoomProcessor {
    private String name;
    public RegularRoom(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础客房：" + name); }
}

```

```java
// RoomProcessor.java
package com.exam.hotel;
public interface RoomProcessor { void process(); }

```

```java
// FileStorage.java
package com.exam.hotel;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

```java
// VIPRoom.java
package com.exam.hotel;
public class VIPRoom implements RoomProcessor {
    private String name;
    public VIPRoom(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级客房：" + name); }
}

```

