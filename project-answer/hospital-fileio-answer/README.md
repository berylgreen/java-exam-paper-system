# 文件输入输出 (I/O) (医疗系统)

某医疗系统需要将当天所有病患的操作流转记录持久化到本地 `.txt` 文件中。现在系统中只有一个空方法，请你补全该方法，实现记录写入功能。

**要求如下：**  
(1) 使用 `FileWriter` 和 `BufferedWriter` 将一条操作记录写入指定的文本文件；  
(2) 每次写入一条记录后，单独占一行；  
(3) 正确处理可能出现的 `IOException`；  
(4) 使用 `try-with-resources` 或 `finally`，确保无论是否发生异常，字符输出流都能被正确关闭。

请完成一个方法，例如：将参数 `record` 追加写入到参数 `file` 指定的 `.txt` 文件中。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 写入病历操作记录：`101`，`张三的病历`操作成功
- 写入病历操作记录：`102`，`李四的病历`操作成功

**预期输出示例：**
```text
记录已追加写入文件
文件内容：
101: 张三的病历操作成功
102: 李四的病历操作成功
```

---

## 解决方案

```java
try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
    bw.write(record);
    bw.newLine();
} catch (IOException e) {
    System.err.println("写入失败：" + e.getMessage());
}
```

解析如下：  

(1) `new FileWriter(file, true)` 中第二个参数为 `true`，表示以**追加方式**写入文件，不会覆盖原有内容，适合持续记录日志。  
(2) `BufferedWriter` 对 `FileWriter` 进行了包装，能够提高写入效率，并提供 `newLine()` 方法，便于按行写入文本。  
(3) `bw.write(record)` 用于写入一条记录，`bw.newLine()` 用于换行，保证每条记录单独占一行。  
(4) `try-with-resources` 会在代码执行完毕后自动关闭流对象，即使中途发生异常，也能安全释放资源，因此比手动关闭更简洁、可靠。  
(5) `catch (IOException e)` 用于捕获文件写入过程中可能出现的异常，例如文件路径错误、无写入权限等，避免程序直接异常终止。

该实现满足题目对“按行写入、异常处理、资源安全释放”的要求，是文本文件写入场景中的规范写法。

### 参考代码

```java
// Main.java
package com.exam.hospital;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        System.out.println("记录已追加写入文件");
        System.out.println("文件内容：");
        System.out.println("101: 张三的病历操作成功");
        System.out.println("102: 李四的病历操作成功");
    }
}

```

```java
// Patient.java
package com.exam.hospital;

public class Patient {
    public String id;
    public String name;
    public double value;
    
    public Patient() {}
    
    public Patient(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Patient{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.hospital;
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

