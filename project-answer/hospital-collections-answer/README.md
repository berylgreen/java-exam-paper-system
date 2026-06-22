# 集合框架 (List/Set) (医疗系统)

某医疗系统早期使用**定长对象数组**存放病患信息。随着病患数量增加，原有方案逐渐暴露出以下问题：

1. 数组长度固定，数据增多时容易出现容量不足或越界问题；
2. 难以高效判断并过滤重复病患；
3. 缺少便捷的排序能力，不利于按业务规则展示病患列表。

**编程要求：**
1. 使用 `ArrayList` 或 `HashSet` 替代原有定长数组来管理病患集合；
2. 设计 `Patient` 类，并根据业务主键（如病患编号 `id`）重写 `equals()` 和 `hashCode()`，实现重复病患判定；
3. 为 `Patient` 类提供排序能力，可通过实现 `Comparable<Patient>` 接口或创建 `Comparator` 按病患编号升序排序；
4. 在 `main` 方法中演示以下过程：
   - 向集合中添加若干病患对象；
   - 包含至少一个重复病患对象；
   - 完成去重后，将结果转换为可排序集合并排序输出。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 依次添加以下病历：
  - `103`，`王五的病历`
  - `101`，`张三的病历`
  - `102`，`李四的病历`
  - `102`，`李四的病历` (重复)

**预期输出示例：**
```text
添加后去重的病历数量：3
排序后输出：
id=101: 张三的病历
id=102: 李四的病历
id=103: 王五的病历
```

---

## 解决方案

```java
// 参考思路说明：
// 1. 若仅解决“动态扩容”问题，可以使用 ArrayList；
// 2. 若同时希望“自动去重”，使用 HashSet 更合适；
// 3. HashSet 判断两个对象是否重复，依赖 equals() 和 hashCode()；
// 4. 因此需要在 Patient 类中根据业务主键（如 id）重写这两个方法；
// 5. 为了实现排序，可以让 Patient 实现 Comparable<Patient> 接口或创建 Comparator，在 compareTo() 中定义排序规则；
// 6. 由于 HashSet 本身无序，若要排序输出，可先将其转换为 ArrayList，再调用 Collections.sort()。
```

本题的关键在于**根据需求选择合适的集合类型**：

1. **使用集合替代定长数组**  
   传统数组长度固定，不适合病患数量动态变化的场景。`ArrayList` 支持动态扩容，`HashSet` 也能存储动态数量的对象，因此都比定长数组更适合。

2. **使用 `HashSet` 实现去重**  
   本题不仅要求替换数组，还要求能过滤重复病患，因此示例中优先使用 `HashSet`。不过，`HashSet` 判断重复不是只看对象地址，而是依赖：
   - `equals()`：判断两个对象在业务上是否相同；
   - `hashCode()`：配合哈希结构提高查找与判重效率。

3. **重写 `equals()` 和 `hashCode()` 的依据**  
   病患去重通常应依据业务主键，例如病患编号 `id`。因此当两个 `Patient` 对象的 `id` 相同时，应视为同一病患，即使姓名字符串不同，也应判定为重复数据。

4. **实现排序功能**  
   题目要求“按某种业务规则排序”，示例中采用“按病患编号升序排序”。因此让 `Patient` 实现 `Comparable<Patient>`，并在 `compareTo()` 方法中比较 `id`。

5. **先去重，再排序输出**  
   `HashSet` 的特点是无序，因此不能直接得到排序结果。常见做法是：
   - 先用 `HashSet<Patient>` 存储并去重；
   - 再转换为 `List<Patient>`；
   - 最后使用 `Collections.sort()` 完成排序。

6. **示例运行结果说明**  
   示例中加入了两个编号同为 `P002` 的病患对象。由于 `equals()` 和 `hashCode()` 都基于 `id` 实现，因此集合中最终只会保留一个 `P002` 对象。排序后输出顺序为 `P001`、`P002`、`P003`。

该实现完整满足题目要求：既解决了数组容量固定的问题，又通过集合与对象方法重写实现了去重和排序。

### 参考代码

```java
// Main.java
package com.exam.hospital;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Patient> set = new HashSet<>();
        set.add(new Patient("103", "王五的病历"));
        set.add(new Patient("101", "张三的病历"));
        set.add(new Patient("102", "李四的病历"));
        set.add(new Patient("102", "李四的病历")); // 重复对象
        System.out.println("添加后去重的病历数量：" + set.size());
        List<Patient> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Patient item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}

```

```java
// Patient.java
package com.exam.hospital;
import java.util.Objects;
public class Patient implements Comparable<Patient> {
    private String id;
    private String name;
    private double value;
    public Patient() {}
    public Patient(String id, String name) { this.id = id; this.name = name; }
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
        Patient that = (Patient) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public int compareTo(Patient other) { return this.id.compareTo(other.id); }
    @Override
    public String toString() { return "Patient{id='" + id + "', name='" + name + "'}"; }
}

```

