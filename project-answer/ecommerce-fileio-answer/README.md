# 文件输入输出 (I/O) (电商系统)

某电商系统需要将当天的订单操作流转记录追加保存到本地 `.txt` 文件中。现在请补全一个日志写入方法，实现记录持久化。

**要求：**
1. 使用 `FileWriter` 与 `BufferedWriter` 将一条订单操作记录写入指定文本文件。
2. 每条记录单独占一行。
3. 写入时采用**追加模式**，避免覆盖已有内容。
4. 正确处理可能出现的 `IOException`。
5. 使用 `try-with-resources` 或 `finally` 确保流对象被安全关闭。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 写入商品操作记录：`101`，`笔记本电脑`操作成功
- 写入商品操作记录：`102`，`智能手机`操作成功

### 预期输出示例
```text
记录已追加写入文件
文件内容：
101: 笔记本电脑操作成功
102: 智能手机操作成功
```

---

## 解决方案

```java
new FileWriter(file, true)
```
上述代码中的第二个参数 `true` 表示以**追加模式**打开文件，这样新写入的订单记录会添加到原有内容之后，而不会覆盖已有数据。

```java
BufferedWriter bw = new BufferedWriter(...)
```
使用 `BufferedWriter` 可以减少实际的磁盘写入次数，提高写入效率，适合按行写入文本内容。

```java
bw.write(record);
bw.newLine();
```
先写入一条订单记录，再通过 `newLine()` 输出换行符，从而保证“每条记录占一行”的要求。

```java
try (...) { ... } catch (IOException e) { ... }
```
这里使用了 `try-with-resources`，无论写入成功还是发生异常，系统都会自动关闭流资源，避免资源泄漏。`catch` 块用于捕获并处理 `IOException`，保证程序在出现 I/O 问题时能够给出错误提示。

综上，该实现满足题目中关于**按行写入、追加保存、异常处理和资源释放**的全部要求。

### 参考代码

```java
package com.exam.ecommerce;
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
        System.out.println("101: 笔记本电脑操作成功");
        System.out.println("102: 智能手机操作成功");
    }
}

```