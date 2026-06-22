# 泛型编程 (通用储物箱)

系统中由于历史遗留原因，存在多个功能完全相同、仅保存数据类型不同的“箱子”类，例如 `StringBox` 和 `IntegerBox`。这会造成代码重复，不利于后续维护与扩展。

请使用 **泛型** 对该设计进行重构，完成以下任务：

(1) 删除原有仅因数据类型不同而重复定义的实体类。
(2) 定义一个泛型类 `Box<T>`，其中包含：
   - 一个类型为 `T` 的成员变量 `item`
   - `set(T item)` 方法，用于存入数据
   - `get()` 方法，用于取出数据
(3) 在 `Main` 类中分别创建 `Box<String>` 和 `Box<Integer>` 对象，验证该泛型类能够正确存储并返回不同类型的数据。

要求程序结构清晰，能够体现泛型类提高代码复用性和类型安全性的作用。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 依次添加以下对象：
  - `103`，`对象C`
  - `101`，`对象A`
  - `102`，`对象B`
  - `102`，`对象B` (重复)

**预期输出示例：**
```text
添加后去重的对象数量：3
排序后输出：
id=101: 对象A
id=102: 对象B
id=103: 对象C
```

---

## 解决方案

本题核心是使用 **泛型类** 代替多个仅数据类型不同的重复类。

### 1. 为什么要使用泛型
如果分别定义 `StringBox`、`IntegerBox` 等类，它们的成员变量、`set` 方法和 `get` 方法的逻辑都相同，只是类型不同。这种写法会产生大量重复代码。

泛型类 `Box<T>` 中的 `T` 表示“类型参数”，在创建对象时再指定实际类型：
- `Box<String>`：表示该箱子只能存放 `String`
- `Box<Integer>`：表示该箱子只能存放 `Integer`

这样既提高了代码复用性，也保证了编译期的类型安全。

### 2. 代码说明
```java
class Box<T> {
    private T item;
```
- 定义泛型类 `Box<T>`
- `item` 的类型由 `T` 决定

```java
public void set(T item) {
    this.item = item;
}
```
- `set` 方法用于向箱子中存入一个 `T` 类型的数据

```java
public T get() {
    return item;
}
```
- `get` 方法返回箱子中保存的数据，返回值类型同样是 `T`

### 3. 测试过程说明
```java
Box<String> stringBox = new Box<>();
stringBox.set("Hello, Generics");
```
- 创建一个只能存放字符串的 `Box`

```java
Box<Integer> integerBox = new Box<>();
integerBox.set(100);
```
- 创建一个只能存放整数的 `Box`

通过输出 `get()` 的结果，可以验证同一个泛型类能够适配不同的数据类型。

### 4. 本题结论
使用 `Box<T>` 后，可以用一个类统一替代多个重复的箱子类，达到：
- 减少重复代码
- 提高可维护性
- 保证类型安全
- 增强程序的扩展性

因此，本题的关键是正确定义泛型类，并在 `Main` 中分别使用不同的具体类型进行测试。

### 参考代码

```java
// Main.java
package com.exam.generic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<MyObject> set = new HashSet<>();
        set.add(new MyObject("103", "对象C"));
        set.add(new MyObject("101", "对象A"));
        set.add(new MyObject("102", "对象B"));
        set.add(new MyObject("102", "对象B")); // 重复对象
        System.out.println("添加后去重的对象数量：" + set.size());
        List<MyObject> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (MyObject item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}

```

```java
// FileStorage.java
package com.exam.generic;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

```java
// MyObject.java
package com.exam.generic;
import java.util.Objects;
public class MyObject implements Comparable<MyObject> {
    private String id;
    private String name;
    private double value;
    public MyObject() {}
    public MyObject(String id, String name) { this.id = id; this.name = name; }
    public String getId() { return id; }
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("编号不能为空");
        this.id = id;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getValue() { return value; }
    public void setValue(double value) {
        if (value < 0) throw new IllegalArgumentException("数值不能为负数");
        this.value = value;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyObject that = (MyObject) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public int compareTo(MyObject other) { return this.id.compareTo(other.id); }
    @Override
    public String toString() { return "MyObject{id='" + id + "', name='" + name + "'}"; }
}

```

