# 文件输入输出 (I/O) (智能家居)

智能家居系统需要将当天的设备操作记录持久化到本地 `.txt` 文件中。请补全一个方法，实现将一条操作记录追加写入指定文本文件。

**要求：**
1. 使用 `FileWriter` 和 `BufferedWriter` 按行写入文件。
2. 每次写入一条记录后换行，便于后续查看和统计。
3. 正确处理可能出现的 `IOException` 异常。
4. 使用 `try-with-resources` 或 `finally` 确保流对象被安全关闭，避免资源泄漏。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 写入设备操作记录：`101`，`智能灯`操作成功
- 写入设备操作记录：`102`，`智能空调`操作成功

### 预期输出示例
```text
记录已追加写入文件
文件内容：
101: 智能灯操作成功
102: 智能空调操作成功
```

---

## 解决方案

```java
new FileWriter(file, true)
```
上述代码中的 `true` 表示以**追加模式**打开文件，这样新记录会写到文件末尾，不会覆盖原有内容。

```java
BufferedWriter bw = new BufferedWriter(...)
```
使用 `BufferedWriter` 可以减少实际磁盘写入次数，提高写入效率。

```java
bw.write(record);
bw.newLine();
```
先写入一条记录，再调用 `newLine()` 进行换行，能够保证每条操作记录单独占一行，便于后续读取和分析。

```java
try (...) { ... } catch (IOException e) { ... }
```
这里使用了 `try-with-resources`，当代码执行结束或发生异常时，系统会自动关闭流对象，无需手动在 `finally` 中释放资源，更安全也更简洁。

同时，通过捕获 `IOException`，可以在文件不存在、路径错误或写入失败时进行异常处理，增强程序的健壮性。

### 参考代码

```java
import java.io.*;

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
