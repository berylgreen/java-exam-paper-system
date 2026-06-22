# 多态与继承 (医疗系统)

某医疗系统中有一个功能庞大的统计类，用于处理不同类型的病患对象。原有实现采用了大量类型判断，例如：`if (obj instanceof X) ... else if (obj instanceof Y) ...`，导致代码难以维护，也不便于后续扩展。

请使用面向对象中的**多态**思想对该设计进行重构，要求如下：
1. 设计一个通用的 `Patient` 抽象类或接口，并声明一个用于处理病患业务的抽象方法。
2. 编写多个具体病患类型子类，在各自的类中实现对应的处理逻辑。
3. 重构统计类，使其只依赖 `Patient` 抽象类型完成统一处理，不再出现任何 `instanceof` 或类型强制转换。

请给出完整的示例代码，并体现多态调用的实现方式。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础病历对象：`张三的病历`
- 创建高级病历对象：`李四的病历`
- 使用统一方法遍历调用处理逻辑

**预期输出示例：**
```text
统一处理基础病历：张三的病历
统一处理高级病历：李四的病历
```

---

## 解决方案

本题的核心是使用**继承（或实现接口）+ 方法重写 + 父类引用指向子类对象**来实现多态，从而替代大量的类型判断。

### 设计思路
1. **抽象父类 `Patient`**
   - 提取所有病患类型的共同行为，定义抽象方法 `process()`。
   - 这样统计类只需要面向 `Patient` 编程，而不需要关心具体是哪个子类。

2. **具体子类分别实现业务逻辑**
   - `RegularPatient`、`VIPPatient`、`EmergencyPatient` 等子类根据自身特点重写 `process()` 方法。
   - 不同对象在调用同一个方法时，会表现出不同的行为，这就是多态。

3. **统计类中消除类型判断**
   - 原来可能写成：

```java
if (obj instanceof RegularPatient) {
    ...
} else if (obj instanceof VIPPatient) {
    ...
} else if (obj instanceof EmergencyPatient) {
    ...
}
```

   - 重构后只需：

```java
patient.process();
```

   - 具体执行哪个版本的方法，由对象的实际类型决定。

### 这样设计的优点
- **提高可维护性**：避免冗长的 `if...else if...` 结构。
- **符合开闭原则**：如果将来新增 `ChildPatient`、`SeniorPatient` 等类型，只需新增子类并实现 `process()`，无需修改统计类。
- **增强可扩展性**：调用方代码更稳定，系统结构更清晰。

### 总结
本题通过将“不同病患的处理逻辑”下放到各自的子类中，实现了“统一接口、不同实现”的多态效果。统计类不再依赖具体类型判断，而是通过调用抽象方法完成处理，这正是面向对象设计中消除分支判断、提升扩展性的常见做法。

### 参考代码

```java
// Main.java
package com.exam.hospital;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        PatientProcessor[] processors = { new Outpatient("张三的病历"), new Emergency("李四的病历") };
        for (PatientProcessor p : processors) { p.process(); }
    }
}

```

```java
// Emergency.java
package com.exam.hospital;
public class Emergency implements PatientProcessor {
    private String name;
    public Emergency(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级病历：" + name); }
}

```

```java
// Outpatient.java
package com.exam.hospital;
public class Outpatient implements PatientProcessor {
    private String name;
    public Outpatient(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础病历：" + name); }
}

```

```java
// Patient.java
package com.exam.hospital;

public class Patient {
    public String id;
    public String name;
    public double value;
    
    public Patient() {}
    
    public Patient(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Patient{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.hospital;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

```java
// PatientProcessor.java
package com.exam.hospital;
public interface PatientProcessor { void process(); }

```

