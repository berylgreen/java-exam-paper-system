# 文件输入输出 (I/O) (银行系统)

某银行系统需要将当天所有账户操作记录持久化到本地 `.txt` 文件中。现在系统中只有一个空方法，请你完成该功能。

**要求：**
1. 使用 `FileWriter` 和 `BufferedWriter` 将一条操作记录追加写入指定文本文件。
2. 每条记录单独占一行。
3. 正确处理可能出现的 `IOException` 异常。
4. 使用 `try-with-resources` 或 `finally` 确保无论是否发生异常，流资源都能被正确关闭。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 写入账户操作记录：`101`，`张三的账户`操作成功
- 写入账户操作记录：`102`，`李四的账户`操作成功

### 预期输出示例
```text
记录已追加写入文件
文件内容：
101: 张三的账户操作成功
102: 李四的账户操作成功
```

---

## 解决方案

```java
try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true)))
```
以上代码使用了 `try-with-resources`，可以在代码执行结束后自动关闭流，即使发生异常也能保证资源被释放。

```java
new FileWriter(file, true)
```
这里第二个参数为 `true`，表示以**追加模式**写入文件，不会覆盖原有内容，适合连续记录账户操作日志。

```java
bw.write(record);
bw.newLine();
```
先写入一条记录，再调用 `newLine()` 换行，保证每条操作记录各占一行，便于后续查看和处理。

```java
catch (IOException e) {
    System.err.println("写入失败：" + e.getMessage());
}
```
用于捕获文件写入过程中可能出现的输入输出异常，例如文件路径错误、无写入权限等，避免程序直接异常终止。

此外，`BufferedWriter` 在 `FileWriter` 外层提供缓冲功能，可以减少实际磁盘写入次数，提高写入效率，适合日志类文本数据的输出。```

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
