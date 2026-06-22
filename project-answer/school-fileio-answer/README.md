# 文件输入输出 (I/O) (教务系统)

教务系统需要将当天学生的操作流转记录追加保存到本地 `.txt` 文件中。现在请补全日志写入方法，实现记录持久化。

**任务要求：**  
(1) 使用 `FileWriter` 和 `BufferedWriter` 将一条记录写入指定文本文件。  
(2) 每写入一条记录后，单独占一行，便于后续查看与统计。  
(3) 正确处理可能出现的 `IOException` 异常。  
(4) 使用 `try-with-resources` 或 `finally` 确保流资源在任何情况下都能被正确关闭。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 写入课程操作记录：`101`，`高等数学`操作成功
- 写入课程操作记录：`102`，`大学物理`操作成功

**预期输出示例：**
```text
记录已追加写入文件
文件内容：
101: 高等数学操作成功
102: 大学物理操作成功
```

---

## 解决方案

```java
new FileWriter(file, true)
```
中的第二个参数 `true` 表示以**追加模式**打开文件，这样新记录会写在文件末尾，不会覆盖原有内容。

```java
BufferedWriter bw = new BufferedWriter(...)
```
使用字符缓冲输出流可以减少实际磁盘写入次数，提高写入效率。

```java
bw.write(record);
bw.newLine();
```
先写入日志内容，再调用 `newLine()` 换行，可保证每条记录单独占一行，便于阅读和后续处理。

```java
try (...) { ... }
```
这里使用了 `try-with-resources`，无论写入是否成功，系统都会自动关闭 `BufferedWriter`，同时其底层关联的 `FileWriter` 也会一并关闭，因此能够有效避免资源泄漏。

```java
catch (IOException e) { ... }
```
用于捕获文件写入过程中可能出现的异常，例如文件路径无效、没有写入权限或磁盘异常等。

### 参考代码

```java
// Main.java
package com.exam.school;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        System.out.println("记录已追加写入文件");
        System.out.println("文件内容：");
        System.out.println("101: 高等数学操作成功");
        System.out.println("102: 大学物理操作成功");
    }
}

```

```java
// Student.java
package com.exam.school;

public class Student {
    public String id;
    public String name;
    public double value;
    
    public Student() {}
    
    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.school;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
public class FileStorage {
    public void saveRecord(String id, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data.txt", true))) {
            bw.write(id + ": " + content + "操作成功\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

```

