# 集合框架 (List/Set) (酒店系统)

酒店管理系统早期使用定长对象数组保存房间信息。随着房间数量增加，这种方式容易出现容量不足、数组越界、插入删除不便等问题；同时，若缺少合理的判重机制，还可能出现重复房间数据。

请使用集合框架对该功能进行改造，要求如下：
1. 使用 `ArrayList` 或 `HashSet` 作为房间数据的存储结构，替代原有定长数组。
2. 定义 `Room` 类，并重写 `equals()` 和 `hashCode()` 方法，使集合能够按照业务规则识别重复房间。
3. 为 `Room` 类提供排序能力，可通过实现 `Comparable<Room>`接口或创建 `Comparator`，按照某一合理业务字段（如房间编号）进行排序。
4. 编写示例代码，演示以下过程：添加房间对象、自动去重、转换为可排序集合并输出排序结果。


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
import java.util.*;

class Room implements Comparable<Room> {
    private String roomId;

    public Room(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(roomId, room.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }

    @Override
    public int compareTo(Room other) {
        return this.roomId.compareTo(other.roomId);
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId='" + roomId + '\'' +
                '}';
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Room> roomSet = new HashSet<>();

        roomSet.add(new Room("A002"));
        roomSet.add(new Room("A001"));
        roomSet.add(new Room("A003"));
        roomSet.add(new Room("A002")); // 重复房间，不能重复加入

        List<Room> roomList = new ArrayList<>(roomSet);
        Collections.sort(roomList);

        for (Room room : roomList) {
            System.out.println(room);
        }
    }
}
```
