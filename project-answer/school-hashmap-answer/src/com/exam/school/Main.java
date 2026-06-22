package com.exam.school;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Storage storage = new Storage();
        storage.add(new Student("101", "高等数学"));
        storage.add(new Student("102", "大学物理"));
        storage.add(new Student("103", "计算机科学"));
        System.out.println("添加后课程数量：" + storage.size());
        Student item = storage.get("102");
        System.out.println("查询 id=102 的课程：" + (item != null ? item.getName() : "null"));
        storage.remove("102");
        System.out.println("删除后再次查询 id=102：" + (storage.get("102") != null ? storage.get("102").getName() : "未找到"));
    }
}
