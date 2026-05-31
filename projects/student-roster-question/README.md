# 集合框架 (学生名单管理器)\n\n现有系统使用固定长度的数组来管理学生，这导致学生数量超过数组大小时报错，且无法有效防止录入重复的学生学号。

**任务要求**：
1. 将 `StudentManager` 内部的存储结构由数组改为 `HashSet<Student>`（实现自动去重）或 `ArrayList` 并手动去重。
2. 完善 `Student` 类，重写 `equals` 和 `hashCode` 方法（以 `studentId` 为准判断相同）。
3. 让 `Student` 实现 `Comparable<Student>` 接口，使名单按学号升序排序。
4. 在 `Main` 类中遍历并打印排序去重后的学生信息。