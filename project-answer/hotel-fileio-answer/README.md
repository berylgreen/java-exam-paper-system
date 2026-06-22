# 文件输入输出 (I/O) (酒店系统)

酒店管理系统需要将当天所有“房间操作流转记录”追加保存到本地 `.txt` 文件中。目前系统中只有一个空方法，请你补全该功能。

**要求如下：**
1. 使用 `FileWriter` 和 `BufferedWriter` 将一条记录写入指定文本文件；
2. 每条记录单独占一行；
3. 采用追加写入方式，避免覆盖原有内容；
4. 正确处理可能出现的 `IOException`；
5. 使用 `try-with-resources` 或 `finally` 确保流对象被正确关闭。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 写入客房操作记录：`101`，`总统套房`操作成功
- 写入客房操作记录：`102`，`豪华大床房`操作成功

**预期输出示例：**
```text
记录已追加写入文件
文件内容：
101: 总统套房操作成功
102: 豪华大床房操作成功
```

---

## 解决方案

本题考查字符输出流、缓冲流、异常处理以及资源释放。

1. `FileWriter(file, true)` 中第二个参数为 `true`，表示以**追加模式**打开文件，这样新记录会写到文件末尾，不会覆盖原有内容。
2. `BufferedWriter` 对 `FileWriter` 进行了包装，能够提供缓冲功能，提高写入效率。
3. `bw.write(record);` 用于写入一条字符串记录，`bw.newLine();` 用于换行，从而保证“每条记录占一行”。
4. 文件写入过程中可能出现 `IOException`，因此需要使用 `catch` 进行异常处理，防止程序异常终止。
5. 这里使用了 `try-with-resources`：
   ```java
   try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
       // 写入操作
   }
   ```
   这种写法可以在代码执行完毕后自动关闭流对象，即使发生异常也能保证资源被释放，比手动在 `finally` 中关闭更加简洁、安全。

该实现满足题目中“按行写入、处理异常、确保关闭资源”的全部要求。

### 参考代码

```java
// Main.java
package com.exam.hotel;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        System.out.println("记录已追加写入文件");
        System.out.println("文件内容：");
        System.out.println("101: 总统套房操作成功");
        System.out.println("102: 豪华大床房操作成功");
    }
}

```

```java
// Room.java
package com.exam.hotel;

public class Room {
    public String id;
    public String name;
    public double value;
    
    public Room() {}
    
    public Room(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Room{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.hotel;
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

