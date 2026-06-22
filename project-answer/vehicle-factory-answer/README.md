# 设计模式 (交通工具制造厂)

为减少业务代码中频繁直接使用 `new Car()`、`new Truck()` 带来的耦合问题，请使用**简单工厂模式**对车辆对象的创建过程进行统一封装。

请根据以下要求完成程序设计：  

(1) 定义车辆接口 `Vehicle`，并在接口中声明 `drive()` 方法。  
(2) 编写 `Car` 和 `Truck` 两个实现类，分别实现 `Vehicle` 接口。  
(3) 创建一个静态简单工厂类 `VehicleFactory`，并提供方法 `Vehicle createVehicle(String type)`。  
(4) 在工厂方法中，根据传入的字符串类型（如 `"car"` 或 `"truck"`）创建并返回对应的车辆对象。  
(5) 修改主程序，不再直接使用 `new` 创建 `Car` 或 `Truck` 对象，而是通过 `VehicleFactory` 获取对象后调用其 `drive()` 方法。

要求：当传入类型不合法时，程序应给出合理的异常提示。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础交通工具对象：`汽车`
- 创建高级交通工具对象：`卡车`
- 使用统一方法遍历调用处理逻辑

**预期输出示例：**
```text
统一处理基础交通工具：汽车
统一处理高级交通工具：卡车
```

---

## 解决方案

本题考查**简单工厂模式**的基本应用，其核心思想是：**将对象的创建过程集中到工厂类中统一管理，而不是分散在业务代码中直接实例化对象**。

### 1. 为什么要使用工厂类
如果主程序中到处写：
```java
new Car();
new Truck();
```
会带来两个问题：

- **客户端依赖具体实现类**，耦合度较高；
- 如果对象创建逻辑发生变化，例如构造参数变多、初始化步骤变复杂，就需要修改很多地方。

使用 `VehicleFactory` 后，客户端只依赖 `Vehicle` 接口和工厂类，无需关心具体创建的是 `Car` 还是 `Truck`。

### 2. 各部分作用说明
- `Vehicle`：定义统一的行为规范，即 `drive()` 方法；
- `Car`、`Truck`：分别实现具体的驾驶行为；
- `VehicleFactory`：根据传入的 `type` 决定创建哪一种车辆对象；
- `Main`：通过工厂获取对象并使用，避免直接 `new` 具体类。

### 3. 工厂方法的实现要点
在 `createVehicle(String type)` 中：
- 使用 `equalsIgnoreCase()` 可以兼容大小写输入；
- 对 `null` 进行判断可以提高程序健壮性；
- 当类型不支持时，抛出 `IllegalArgumentException`，便于及时发现错误输入。

### 4. 该设计的优点
- **降低耦合**：客户端只面向接口编程；
- **集中管理创建逻辑**：后续修改创建方式时只需改工厂类；
- **提高可维护性**：代码结构更清晰。

### 5. 说明
本题使用的是**简单工厂模式**，适合产品种类较少且创建逻辑需要统一管理的场景。如果后续车辆类型不断增加，简单工厂可能需要频繁修改，此时可以进一步考虑工厂方法模式等更灵活的设计方案。

### 参考代码

```java
// Main.java
package com.exam.vehicle;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        VehicleProcessor[] processors = { new Car("汽车"), new Truck("卡车") };
        for (VehicleProcessor p : processors) { p.process(); }
    }
}

```

```java
// VehicleProcessor.java
package com.exam.vehicle;
public interface VehicleProcessor { void process(); }

```

```java
// Car.java
package com.exam.vehicle;
public class Car implements VehicleProcessor {
    private String name;
    public Car(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础交通工具：" + name); }
}

```

```java
// Vehicle.java
package com.exam.vehicle;

public class Vehicle {
    public String id;
    public String name;
    public double value;
    
    public Vehicle() {}
    
    public Vehicle(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Vehicle{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.vehicle;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

```java
// Truck.java
package com.exam.vehicle;
public class Truck implements VehicleProcessor {
    private String name;
    public Truck(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级交通工具：" + name); }
}

```

