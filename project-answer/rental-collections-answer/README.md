# 集合框架 (List/Set) (租车系统)

某租车系统过去使用定长对象数组来存放和管理车辆列表。随着车辆数据不断增加，这种方式逐渐暴露出两个问题：一是数组长度固定，容易出现容量不足或数组越界；二是难以根据业务规则高效地去除重复车辆。

请基于 Java 集合框架对该功能进行改造，要求如下：
1. 使用 `ArrayList` 或 `HashSet` 替代原有的对象数组来保存车辆信息。
2. 设计 `Vehicle` 类，并重写 `equals()` 和 `hashCode()` 方法，使程序能够按照业务主键（如车辆编号）判断两辆车是否为同一辆车。
3. 提供集合元素的排序功能，可通过实现 `Comparable<Vehicle>` 接口或创建 `Comparator` 完成。
4. 编写示例代码，演示车辆的添加、去重与排序过程。


---

## 解决方案

```java
// 运行结果示例
Vehicle{id='A001', brand='Honda'}
Vehicle{id='A002', brand='Toyota'}
Vehicle{id='A003', brand='BMW'}
```

本题的关键在于用集合框架替代定长数组，并结合集合的特性完成“动态存储、去重、排序”三个目标。

1. **使用集合替代数组**
   - `ArrayList` 支持动态扩容，适合按顺序存储车辆对象。
   - `HashSet` 不允许存放重复元素，适合实现去重。
   - 因此，本题可以先将车辆存入 `HashSet` 完成去重，再转换为 `ArrayList` 进行排序和遍历。

2. **重写 `equals()` 和 `hashCode()` 的原因**
   - `HashSet` 判断元素是否重复时，依赖 `hashCode()` 和 `equals()`。
   - 如果不重写这两个方法，即使两个 `Vehicle` 对象的车辆编号相同，也会被视为不同对象。
   - 题目中“业务上的同一辆车”通常可由车辆编号 `id` 唯一确定，因此在 `equals()` 和 `hashCode()` 中只使用 `id` 参与比较即可。

3. **实现排序功能**
   - 让 `Vehicle` 实现 `Comparable<Vehicle>`接口或创建 `Comparator`，并重写 `compareTo()` 方法。
   - 示例中按照车辆编号 `id` 的字典序升序排列。
   - 排序时先将 `HashSet` 转换为 `ArrayList`，再调用 `Collections.sort()` 完成排序。

4. **程序执行流程**
   - 创建 `HashSet<Vehicle>` 保存车辆。
   - 添加多个车辆对象，其中包含编号重复的车辆。
   - 由于已重写 `equals()` 和 `hashCode()`，重复编号的车辆不会被重复存入。
   - 将去重后的结果转为 `ArrayList<Vehicle>`。
   - 调用 `Collections.sort()` 按车辆编号排序。
   - 最后输出排序后的车辆列表。

5. **总结**
   - `HashSet` 解决了重复元素问题。
   - `ArrayList` 解决了定长数组不便扩容的问题。
   - `Comparable` 提供了统一的排序规则。
   - 这正体现了 Java 集合框架在实际业务开发中的优势。

### 参考代码

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Vehicle implements Comparable<Vehicle> {
    private String id;
    private String brand;

    public Vehicle(String id, String brand) {
        this.id = id;
        this.brand = brand;
    }

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Vehicle other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "Vehicle{id='" + id + "', brand='" + brand + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Vehicle> vehicleSet = new HashSet<>();

        vehicleSet.add(new Vehicle("A002", "Toyota"));
        vehicleSet.add(new Vehicle("A001", "Honda"));
        vehicleSet.add(new Vehicle("A002", "Toyota")); // 重复车辆，无法再次加入
        vehicleSet.add(new Vehicle("A003", "BMW"));

        List<Vehicle> vehicleList = new ArrayList<>(vehicleSet);
        Collections.sort(vehicleList);

        for (Vehicle vehicle : vehicleList) {
            System.out.println(vehicle);
        }
    }
}
```
