# 文件 I/O (日志备份工具)\n\n项目中需要一个日志备份功能，将临时生成的文本文件安全、高效地复制到备份目录下。当前只提供了空的方法存根。

**任务要求**：
1. 使用 Java 字节流 (`FileInputStream` 和 `FileOutputStream`) 实现文件的复制。
2. 为了提高读写效率，要求使用 `BufferedInputStream` 和 `BufferedOutputStream` 进行包装。
3. 在复制过程中正确处理 `IOException`，并在 `finally` 块中（或使用 `try-with-resources` 语法）确保输入输出流被正确关闭。
4. 编写 `Main` 类生成测试文件，并调用备份工具完成复制。