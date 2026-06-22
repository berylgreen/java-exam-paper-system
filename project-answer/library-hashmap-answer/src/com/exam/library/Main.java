package com.exam.library;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Storage storage = new Storage();
        storage.add(new Book("101", "Java编程思想"));
        storage.add(new Book("102", "算法导论"));
        storage.add(new Book("103", "计算机网络"));
        System.out.println("添加后图书数量：" + storage.size());
        Book item = storage.get("102");
        System.out.println("查询 id=102 的图书：" + (item != null ? item.getName() : "null"));
        storage.remove("102");
        System.out.println("删除后再次查询 id=102：" + (storage.get("102") != null ? storage.get("102").getName() : "未找到"));
    }
}
