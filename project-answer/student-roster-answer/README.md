# 集合框架 (学生名单管理器)

某学生管理系统原先使用固定长度数组保存学生信息，存在两个问题：当学生人数超过数组容量时，程序不便扩展；同时也难以高效避免重复录入相同学号的学生。

请根据集合框架知识完成下列改造任务：

1. 将 `StudentManager` 中保存学生信息的结构改为集合类型。可使用 `HashSet<Student>` 实现自动去重。
2. 完善 `Student` 类，重写 `equals` 和 `hashCode` 方法，并规定：如果两个学生的 `studentId` 相同，则认为是同一个学生。
3. 让 `Student` 类实现 `Comparable<Student>`接口或创建 `Comparator`，使学生对象能够按照学号升序排序。
4. 在 `Main` 类中输出“去重并排序后”的学生名单。

要求：程序运行结果应体现出“重复学号不会重复保存”以及“输出结果按学号升序排列”。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 依次添加以下学生：
  - `103`，`小刚`
  - `101`，`小明`
  - `102`，`小红`
  - `102`，`小红` (重复)

### 预期输出示例
```text
添加后去重的学生数量：3
排序后输出：
id=101: 小明
id=102: 小红
id=103: 小刚
```

---

## 解决方案

本题考查集合框架中“去重”与“排序”的配合使用。

1. 使用 `HashSet<Student>` 代替数组
`HashSet` 底层基于哈希表实现，能够动态扩容，不受固定长度限制；同时它不允许存入“逻辑上重复”的元素，因此适合本题的学生去重需求。

2. 为什么必须同时重写 `equals` 和 `hashCode`
在 `HashSet` 中，判断两个对象是否重复，先依赖 `hashCode()`，再结合 `equals()`。
如果只重写其中一个方法，就可能出现“学号相同但仍然无法正确去重”的问题。
本题要求以 `studentId` 作为学生唯一标识，因此这两个方法都应基于 `studentId` 编写。

关键实现如下：
```java
@Override
public boolean equals(Object o) {
    if (this == o) {
        return true;
    }
    if (o == null || getClass() != o.getClass()) {
        return false;
    }
    Student student = (Student) o;
    return Objects.equals(studentId, student.studentId);
}

@Override
public int hashCode() {
    return Objects.hash(studentId);
}
```

3. 为什么要实现 `Comparable<Student>`
`HashSet` 中的元素本身是无序的。如果希望按学号升序输出，就需要先将集合转为 `List`，再排序。
实现 `Comparable<Student>` 后，可以直接使用 `Collections.sort(list)` 完成自然排序。

对应的排序规则为：
```java
@Override
public int compareTo(Student other) {
    return this.studentId.compareTo(other.studentId);
}
```

4. 程序执行流程
先将学生对象加入 `HashSet`，重复学号的对象会被自动过滤；
再把 `HashSet` 转为 `ArrayList`；
最后调用排序方法，按学号升序遍历输出。

5. 结果特点
如果输入了两个学号同为 `1002` 的学生，最终结果中只会保留一个；
输出顺序类似：`1001`、`1002`、`1003`，体现了去重和排序的要求。

因此，本题的核心是：
使用 `HashSet` 实现去重，重写 `equals` 和 `hashCode` 保证去重规则正确，实现 `Comparable` 保证能够按学号排序输出。

### 参考代码

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Student implements Comparable<Student> {
    private String studentId;

    public Student(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public int compareTo(Student other) {
        return this.studentId.compareTo(other.studentId);
    }

    @Override
    public String toString() {
        return "Student{studentId='" + studentId + "'}";
    }
}

class StudentManager {
    private Set<Student> students = new HashSet<>();

    public void addStudent(Student student) {
        students.add(student);
    }

    public List<Student> getSortedStudents() {
        List<Student> list = new ArrayList<>(students);
        Collections.sort(list);
        return list;
    }
}

public class Main {
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();

        manager.addStudent(new Student("1002"));
        manager.addStudent(new Student("1001"));
        manager.addStudent(new Student("1003"));
        manager.addStudent(new Student("1002"));

        List<Student> result = manager.getSortedStudents();
        for (Student student : result) {
            System.out.println(student);
        }
    }
}
```
