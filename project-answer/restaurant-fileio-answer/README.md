# 文件输入输出 (I/O) (餐饮系统)

餐饮系统需要将当天的菜品操作流转记录持久化保存到本地 `.txt` 文件中。现在请补全一个记录写入方法，实现以下功能：

1. 使用 `FileWriter` 与 `BufferedWriter` 将一条记录写入指定文本文件。
2. 每次写入一条记录后换行，便于后续按行查看和读取。
3. 需要正确处理可能出现的 `IOException` 异常。
4. 必须使用 `try-with-resources` 或 `finally` 确保流资源被安全关闭。
5. 为了保留历史记录，要求以**追加写入**方式打开文件，而不是覆盖原有内容。

请编写一个方法，例如 `logRecord(String file, String record)`，完成上述功能。


---

## 解决方案

```java
new FileWriter(file, true)
```
中第二个参数 `true` 表示以**追加模式**打开文件，这样新记录会写到文件末尾，不会覆盖原有内容。

```java
BufferedWriter bw = new BufferedWriter(...)
```
使用缓冲字符输出流可以减少实际磁盘写入次数，提高写入效率。

```java
bw.write(record);
bw.newLine();
```
先写入一条记录，再调用 `newLine()` 换行，保证每条记录单独占一行，便于查看和后续处理。

```java
try (...) { ... } catch (IOException e) { ... }
```
这里使用了 `try-with-resources`，无论写入成功还是发生异常，流都会被自动关闭，因此比手动在 `finally` 中关闭更简洁、安全。

捕获 `IOException` 是因为文件创建、打开、写入或关闭过程中都可能出现输入输出异常。通过异常处理可以避免程序直接崩溃，并输出错误信息方便排查。

因此，该实现满足题目要求：
1. 使用了 `FileWriter` 和 `BufferedWriter`；
2. 按行写入记录；
3. 处理了 `IOException`；
4. 使用 `try-with-resources` 自动释放资源；
5. 采用追加方式保留历史日志。

### 参考代码

```java
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterLogger {
    public void logRecord(String file, String record) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(record);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("写入失败：" + e.getMessage());
        }
    }
}
```
