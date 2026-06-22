package com.exam.ecommerce;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Storage storage = new Storage();
        storage.add(new Order("101", "笔记本电脑"));
        storage.add(new Order("102", "智能手机"));
        storage.add(new Order("103", "蓝牙耳机"));
        System.out.println("添加后商品数量：" + storage.size());
        Order item = storage.get("102");
        System.out.println("查询 id=102 的商品：" + (item != null ? item.getName() : "null"));
        storage.remove("102");
        System.out.println("删除后再次查询 id=102：" + (storage.get("102") != null ? storage.get("102").getName() : "null"));
    }
}
