# 文件输入输出 (I/O) (租车系统)

租车系统需要新增一个日志持久化功能，用于将当天的车辆操作流转记录保存到本地 `.txt` 文件中。现在系统中仅有一个空方法，请你完成该功能。

**任务要求：**
1. 使用 `FileWriter` 和 `BufferedWriter` 将操作记录写入指定的文本文件。
2. 每条记录单独占一行，便于后续查看与统计。
3. 需要正确处理可能出现的 `IOException` 异常。
4. 使用 `try-with-resources` 或 `finally` 确保无论是否发生异常，流资源都能够被正确关闭。
5. 为了避免覆盖原有日志，写入时应采用**追加模式**。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 写入车辆操作记录：`101`，`丰田卡罗拉`操作成功
- 写入车辆操作记录：`102`，`本田雅阁`操作成功

### 预期输出示例
```text
记录已追加写入文件
文件内容：
101: 丰田卡罗拉操作成功
102: 本田雅阁操作成功
```

---

## 解决方案

```java
new FileWriter(file, true)
```
中第二个参数 `true` 表示以**追加方式**打开文件，这样新日志会写入到文件末尾，而不会覆盖已有内容。

```java
BufferedWriter bw = new BufferedWriter(...)
```
使用缓冲字符输出流可以减少实际的磁盘写入次数，提高写入效率。

```java
bw.write(record);
bw.newLine();
```
先写入一条日志内容，再调用 `newLine()` 换行，从而保证“每条记录占一行”的要求。

```java
try (...) { ... } catch (IOException e) { ... }
```
这里使用了 `try-with-resources` 语法：
- 当代码执行完毕时，流会自动关闭；
- 即使写入过程中发生异常，资源也能被安全释放；
- 同时通过 `catch` 捕获 `IOException`，完成异常处理。

因此，该实现满足题目中关于**文件写入、逐行保存、异常处理、资源释放**的全部要求。

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
            System.err.println("写入日志失败：" + e.getMessage());
        }
    }
}
```
