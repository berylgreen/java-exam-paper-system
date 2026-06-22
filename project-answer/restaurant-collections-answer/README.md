# 集合框架 (List/Set) (餐饮系统)

餐饮管理系统原先使用定长对象数组保存菜品列表。随着菜品数量增加，这种方式容易出现容量不足、扩容不便，以及重复菜品难以有效识别等问题。

**请按以下要求完成程序设计：**
1. 使用集合类替代原有数组来管理菜品数据，可根据需求选择 `ArrayList` 或 `HashSet`。
2. 定义 `Dish` 类，并根据业务规则重写 `equals()` 和 `hashCode()` 方法，使系统能够正确判断重复菜品。
3. 为 `Dish` 类提供排序能力，可通过实现 `Comparable<Dish>`接口或创建 `Comparator`，按照某一业务字段（如菜品编号）进行排序。
4. 在主程序中演示以下过程：添加多个菜品对象、去除重复元素、对结果排序并输出。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 依次添加以下菜品：
  - `103`，`红烧肉`
  - `101`，`宫保鸡丁`
  - `102`，`鱼香肉丝`
  - `102`，`鱼香肉丝` (重复)

### 预期输出示例
```text
添加后去重的菜品数量：3
排序后输出：
id=101: 宫保鸡丁
id=102: 鱼香肉丝
id=103: 红烧肉
```

---

## 解决方案

```java
// 程序设计思路：
// 1. 使用 HashSet 存储 Dish 对象，利用集合“元素不可重复”的特性实现去重。
// 2. 要让 HashSet 能正确判断两个 Dish 是否重复，必须在 Dish 类中重写 equals() 和 hashCode()。
//    本题中以菜品编号 id 作为业务上的唯一标识，因此只要 id 相同，就认为是同一道菜。
// 3. 为了支持排序，让 Dish 实现 Comparable<Dish>接口或创建 `Comparator`，并重写 compareTo() 方法。
//    示例中按照 id 的字典序升序排序。
// 4. 由于 HashSet 本身无序，如需排序输出，可先将其转换为 ArrayList，再调用 Collections.sort() 排序。
```

本题核心考查集合框架中不同集合类的特点及其使用方法：

1. `ArrayList` 适合按索引访问、允许重复元素、支持动态扩容。
2. `HashSet` 适合去重，但元素默认无序。
3. `equals()` 与 `hashCode()` 必须保持一致：如果两个对象通过 `equals()` 判断相等，那么它们的 `hashCode()` 也必须相同。
4. 实现 `Comparable<Dish>` 后，可以让集合中的对象按照指定规则自然排序。

在示例中，编号为 `D002` 的两个 `Dish` 对象会被视为重复元素，因此最终只保留一个；之后再按编号排序输出。

### 参考代码

```java
import java.util.*;

class Dish implements Comparable<Dish> {
    private String id;
    private String name;

    public Dish(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Dish other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "Dish{id='" + id + "', name='" + name + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Dish> dishSet = new HashSet<>();

        dishSet.add(new Dish("D002", "宫保鸡丁"));
        dishSet.add(new Dish("D001", "鱼香肉丝"));
        dishSet.add(new Dish("D003", "麻婆豆腐"));
        dishSet.add(new Dish("D002", "宫保鸡丁（重复）"));

        List<Dish> dishList = new ArrayList<>(dishSet);
        Collections.sort(dishList);

        for (Dish dish : dishList) {
            System.out.println(dish);
        }
    }
}
```
