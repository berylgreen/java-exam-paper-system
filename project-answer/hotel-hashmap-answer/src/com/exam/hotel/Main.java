package com.exam.hotel;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Storage storage = new Storage();
        storage.add(new Room("101", "总统套房"));
        storage.add(new Room("102", "豪华大床房"));
        storage.add(new Room("103", "标准间"));
        System.out.println("添加后客房数量：" + storage.size());
        Room item = storage.get("102");
        System.out.println("查询 id=102 的客房：" + (item != null ? item.getName() : "null"));
        storage.remove("102");
        System.out.println("删除后再次查询 id=102：" + (storage.get("102") != null ? storage.get("102").getName() : "null"));
    }
}
