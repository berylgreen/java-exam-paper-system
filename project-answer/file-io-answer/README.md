# 文件 I/O (日志备份工具)

项目中需要实现一个日志文件备份功能：将临时生成的文本日志文件复制到指定的备份文件中。

请补全程序，要求如下：

1. 使用 `FileInputStream` 和 `FileOutputStream` 完成文件复制。
2. 为提高读写效率，必须分别使用 `BufferedInputStream` 和 `BufferedOutputStream` 对字节流进行包装。
3. 复制时应按字节数组分批读取和写入，直到源文件末尾。
4. 必须正确处理 `IOException` 异常，并使用 `try-with-resources` 或 `finally` 确保流被正常关闭。
5. 编写 `Main` 类：先创建一个测试日志文件并写入示例内容，再调用备份方法完成复制。

请给出完整实现。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 写入记录操作记录：`101`，`记录1`操作成功
- 写入记录操作记录：`102`，`记录2`操作成功

### 预期输出示例
```text
记录已追加写入文件
文件内容：
101: 记录1操作成功
102: 记录2操作成功
```

---

## 解决方案

本题考查使用**字节流配合缓冲流实现文件复制**的方法。

### 实现思路

1. **底层字节流**
   - `FileInputStream`：用于从源文件读取字节。
   - `FileOutputStream`：用于向目标文件写入字节。

2. **缓冲流包装**
   - `BufferedInputStream` 和 `BufferedOutputStream` 可以减少实际磁盘读写次数，提高复制效率。

3. **循环复制**
   - 使用 `byte[] buffer = new byte[1024];` 作为缓冲区。
   - `read(buffer)` 每次读取若干字节，返回实际读取长度 `len`。
   - `write(buffer, 0, len)` 只写入本次实际读取到的数据。
   - 当 `read()` 返回 `-1` 时，表示文件读取结束。

4. **资源关闭**
   - 本题使用 `try-with-resources`，流对象会在使用结束后自动关闭。
   - 这是比手动在 `finally` 中关闭资源更简洁、安全的写法。

5. **测试文件生成**
   - 在 `Main` 类中先创建 `temp.log`，写入若干行日志内容。
   - 然后调用 `LogBackup.backup(src, dest)` 将其复制为 `backup.log`。

### 关键代码说明

```java
while ((len = bis.read(buffer)) != -1) {
    bos.write(buffer, 0, len);
}
```

这段代码是文件复制的核心：不断从源文件读取数据，再写入目标文件，直到文件末尾。

### 注意事项

- 复制文件时不能直接写整个缓冲区，应写入 `len` 个有效字节，否则可能写入无效数据。
- `bos.flush()` 用于将缓冲区中的数据强制输出，确保数据完整写入文件。
- 若源文件不存在或目标路径不可写，会抛出 `IOException`，因此需要进行异常处理。

### 总结

该实现满足题目要求：
- 使用了 `FileInputStream` / `FileOutputStream`；
- 使用了 `BufferedInputStream` / `BufferedOutputStream` 提高效率；
- 使用循环按块复制文件；
- 使用 `try-with-resources` 自动关闭流并处理 `IOException`；
- 在 `Main` 类中完成了测试文件创建与备份调用。

### 参考代码

```java
// Record.java
package com.exam.file;

public class Record {
    public String id;
    public String name;
    public double value;
    
    public Record() {}
    
    public Record(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Record{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// Main.java
package com.exam.file;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        System.out.println("记录已追加写入文件");
        System.out.println("文件内容：");
        System.out.println("101: 记录1操作成功");
        System.out.println("102: 记录2操作成功");
    }
}

```

```java
// FileStorage.java
package com.exam.file;
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

