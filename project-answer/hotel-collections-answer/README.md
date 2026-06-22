# 集合框架 (List/Set) (酒店系统)

酒店管理系统早期使用定长对象数组保存房间信息。随着房间数量增加，这种方式容易出现容量不足、数组越界、插入删除不便等问题；同时，若缺少合理的判重机制，还可能出现重复房间数据。

请使用集合框架对该功能进行改造，要求如下：
1. 使用 `ArrayList` 或 `HashSet` 作为房间数据的存储结构，替代原有定长数组。
2. 定义 `Room` 类，并重写 `equals()` 和 `hashCode()` 方法，使集合能够按照业务规则识别重复房间。
3. 为 `Room` 类提供排序能力，可通过实现 `Comparable<Room>`接口或创建 `Comparator`，按照某一合理业务字段（如房间编号）进行排序。
4. 编写示例代码，演示以下过程：添加房间对象、自动去重、转换为可排序集合并输出排序结果。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 依次添加以下客房：
  - `103`，`标准间`
  - `101`，`总统套房`
  - `102`，`豪华大床房`
  - `102`，`豪华大床房` (重复)

**预期输出示例：**
```text
添加后去重的客房数量：3
排序后输出：
id=101: 总统套房
id=102: 豪华大床房
id=103: 标准间
```

---

## 解决方案

```java
// 程序运行后，输出结果类似：
// Room{roomId='A001'}
// Room{roomId='A002'}
// Room{roomId='A003'}
```

解析如下：

1. 使用集合替代定长数组
   - `HashSet` 适合用于“去重存储”，能够避免重复房间对象被加入。
   - `ArrayList` 适合用于“有序遍历和排序后的结果保存”。
   - 因此，本题中先用 `HashSet` 去重，再转为 `ArrayList` 排序，是较合理的实现方式。

2. 重写 `equals()` 和 `hashCode()`
   - 在 Java 中，`HashSet` 判断元素是否重复时，会综合使用 `hashCode()` 和 `equals()`。
   - 如果两个 `Room` 对象的 `roomId` 相同，就应视为同一个业务房间，因此应基于 `roomId` 重写这两个方法。
   - 若只重写 `equals()` 而不重写 `hashCode()`，会破坏哈希集合的判重规则，导致去重失效或行为异常。

3. 实现 `Comparable<Room>`
   - 通过实现 `Comparable<Room>`接口或创建 `Comparator`，可以为 `Room` 指定“默认排序规则”。
   - 本题按房间编号 `roomId` 的字典序升序排列，因此在 `compareTo()` 中使用：
     ```java
     return this.roomId.compareTo(other.roomId);
     ```

4. 处理流程说明
   - 创建若干 `Room` 对象并加入 `HashSet`。
   - 重复的 `A002` 因为业务上相同，不会重复保存。
   - 将 `HashSet` 转换为 `ArrayList` 后，调用 `Collections.sort(roomList)` 完成排序。
   - 最后输出排序后的房间列表。

5. 原答案中的问题
   - 错误地将自定义类命名为 `ArrayList`，会与 Java 集合类 `ArrayList` 冲突，不符合规范。
   - 泛型和对象类型设计不合理，容易造成代码歧义。
   - 改为定义 `Room` 类后，语义更清晰，也更符合题目要求。

本题核心考查的是：
- 集合框架中 `List` 与 `Set` 的适用场景；
- `equals()` 与 `hashCode()` 的重写规则；
- `Comparable` 实现对象排序的基本方法。```

### 参考代码

```java
// Main.java
package com.exam.hotel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Room> set = new HashSet<>();
        set.add(new Room("103", "标准间"));
        set.add(new Room("101", "总统套房"));
        set.add(new Room("102", "豪华大床房"));
        set.add(new Room("102", "豪华大床房")); // 重复对象
        System.out.println("添加后去重的客房数量：" + set.size());
        List<Room> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Room item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}

```

```java
// Room.java
package com.exam.hotel;
import java.util.Objects;
public class Room implements Comparable<Room> {
    private String id;
    private String name;
    private double value;
    public Room() {}
    public Room(String id, String name) { this.id = id; this.name = name; }
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
        Room that = (Room) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public int compareTo(Room other) { return this.id.compareTo(other.id); }
    @Override
    public String toString() { return "Room{id='" + id + "', name='" + name + "'}"; }
}

```

