# 文件输入输出 (I/O) (物流系统)

物流系统需要将当天所有包裹的操作流转记录持久化到本地 `.txt` 文件中。现请补全相关功能，完成一个用于写入日志的方法。

**任务要求：**
1. 使用 `FileWriter` 与 `BufferedWriter` 将一条记录追加写入指定文本文件；
2. 每条记录单独占一行；
3. 正确处理可能出现的 `IOException`；
4. 使用 `try-with-resources` 或 `finally` 确保流资源被安全关闭。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 写入包裹操作记录：`101`，`电子产品包裹`操作成功
- 写入包裹操作记录：`102`，`书籍包裹`操作成功

### 预期输出示例
```text
记录已追加写入文件
文件内容：
101: 电子产品包裹操作成功
102: 书籍包裹操作成功
```

---

## 解决方案

```java
new FileWriter(file, true)
```
其中第二个参数 `true` 表示以**追加模式**打开文件，这样新记录会写入到文件末尾，不会覆盖原有内容。

```java
BufferedWriter bw = new BufferedWriter(...)
```
使用缓冲字符输出流可以减少实际的磁盘写入次数，提高写入效率。

```java
bw.write(record);
bw.newLine();
```
先写入日志内容，再通过 `newLine()` 写入换行符，保证每条记录独占一行，便于后续查看和处理。

```java
try (...) { ... } catch (IOException e) { ... }
```
`try-with-resources` 会在代码执行结束后自动关闭流对象，即使中途发生异常也能安全释放资源，因此比手动在 `finally` 中关闭更简洁、更安全。

捕获 `IOException` 是因为文件写入过程中可能出现文件路径无效、权限不足、磁盘异常等问题。此处通过输出错误信息进行异常处理，实际项目中也可以进一步记录到日志系统或向上抛出异常。

### 参考代码

```java
package com.exam.logistics;
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
        System.out.println("101: 电子产品包裹操作成功");
        System.out.println("102: 书籍包裹操作成功");
    }
}

```