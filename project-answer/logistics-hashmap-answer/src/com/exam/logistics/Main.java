package com.exam.logistics;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Storage storage = new Storage();
        storage.add(new Package("101", "电子产品包裹"));
        storage.add(new Package("102", "书籍包裹"));
        storage.add(new Package("103", "衣物包裹"));
        System.out.println("添加后包裹数量：" + storage.size());
        Package item = storage.get("102");
        System.out.println("查询 id=102 的包裹：" + (item != null ? item.getName() : "null"));
        storage.remove("102");
        System.out.println("删除后再次查询 id=102：" + (storage.get("102") != null ? storage.get("102").getName() : "null"));
    }
}
