# 文件输入输出 (I/O) (图书馆系统)

图书馆系统需要将当天的图书操作记录持久化到本地 `.txt` 文件中。请补全或编写一个方法，实现将一条记录追加写入指定文件。

**要求：**
1. 使用 `FileWriter` 和 `BufferedWriter` 按行写入文本内容。
2. 写入时采用追加模式，避免覆盖原有记录。
3. 正确处理可能出现的 `IOException` 异常。
4. 使用 `try-with-resources` 或 `finally` 确保流对象被安全关闭。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 写入图书操作记录：`101`，`Java编程思想`操作成功
- 写入图书操作记录：`102`，`算法导论`操作成功

### 预期输出示例
```text
记录已追加写入文件
文件内容：
101: Java编程思想操作成功
102: 算法导论操作成功
```

---

## 解决方案

```java
try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true)))
```
这行代码同时完成了两件事：
1. `new FileWriter(file, true)`：以追加模式打开文件，`true` 表示新内容会写到文件末尾，不会覆盖原有内容。
2. `new BufferedWriter(...)`：使用缓冲字符输出流包装 `FileWriter`，可以提高写入效率。

```java
bw.write(record);
bw.newLine();
```
- `write(record)` 用于写入一条记录。
- `newLine()` 用于换行，使每条记录单独占一行，便于后续查看和处理。

```java
catch (IOException e) {
    System.err.println("写入失败：" + e.getMessage());
}
```
文件写入过程中可能因路径错误、权限不足或磁盘问题抛出 `IOException`，因此需要进行异常处理，防止程序异常终止。

使用 `try-with-resources` 的好处是：无论写入成功还是发生异常，流对象都会被自动关闭，避免资源泄露。这也是 Java 中处理 I/O 流的推荐写法。

### 参考代码

```java
package com.exam.library;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
class FileStorage {
    public void saveRecord(String id, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data.txt", true))) {
            bw.write(id + ": " + content + "操作成功\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        System.out.println("记录已追加写入文件");
        System.out.println("文件内容：");
        System.out.println("101: Java编程思想操作成功");
        System.out.println("102: 算法导论操作成功");
    }
}

```