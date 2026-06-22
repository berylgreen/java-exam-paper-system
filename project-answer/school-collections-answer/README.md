# 集合框架 (List/Set) (教务系统)

教务系统原先使用定长对象数组保存学生列表，随着学生数量增加，出现了以下问题：
1. 数组长度固定，数据增加时容易发生越界或需要频繁扩容；
2. 难以高效去除重复学生；
3. 不便于按照业务规则对学生信息进行排序。

**编程要求：**
1. 使用 Java 集合框架中的 `ArrayList` 或 `HashSet` 管理学生对象；
2. 定义 `Student` 类，并根据学生的唯一标识（如学号）重写 `equals()` 和 `hashCode()` 方法，实现业务去重；
3. 为 `Student` 类提供排序能力，可通过实现 `Comparable<Student>`接口或创建 `Comparator`，按照学号升序或其他合理业务规则排序；
4. 编写测试代码，演示学生对象的添加、去重和排序过程。


---

## 解决方案

```java
// 核心实现思路：
// 1. 使用 HashSet 存储学生对象：
//    - 集合长度可动态变化，不会像定长数组那样容易越界；
//    - HashSet 可以结合 equals() 和 hashCode() 自动完成去重。
//
// 2. 在 Student 类中重写 equals() 和 hashCode()：
//    - 按业务语义，学号 id 是学生的唯一标识；
//    - 如果两个 Student 对象的 id 相同，就认为是同一个学生。
//
// 3. 实现 Comparable<Student>接口或创建 `Comparator`：
//    - 在 compareTo() 方法中定义排序规则；
//    - 本题示例按学号升序排序。
//
// 4. 排序过程：
//    - HashSet 本身无序；
//    - 因此先将 HashSet 转为 ArrayList；
//    - 再通过 Collections.sort() 完成排序。
```

本题的关键是正确区分不同集合的用途：
- `ArrayList` 适合按顺序保存元素、支持下标访问；
- `HashSet` 适合去除重复元素；
- 当题目既要求去重又要求排序时，可以先用 `HashSet` 去重，再转为 `ArrayList` 排序。

示例中，学号为 `A002` 的两个对象会被视为重复元素，因此最终只会保留一个。排序后输出结果将按学号从小到大排列。

### 参考代码

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Student implements Comparable<Student> {
    private String id;
    private String name;

    public Student(String id, String name) {
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
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Student other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + name + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Student> studentSet = new HashSet<>();

        studentSet.add(new Student("A002", "张三"));
        studentSet.add(new Student("A001", "李四"));
        studentSet.add(new Student("A002", "王五")); // 学号重复，视为同一学生

        List<Student> studentList = new ArrayList<>(studentSet);
        Collections.sort(studentList);

        for (Student student : studentList) {
            System.out.println(student);
        }
    }
}
```
