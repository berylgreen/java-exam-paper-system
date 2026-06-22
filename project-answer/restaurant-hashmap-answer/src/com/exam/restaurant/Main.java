package com.exam.restaurant;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Storage storage = new Storage();
        storage.add(new Dish("101", "宫保鸡丁"));
        storage.add(new Dish("102", "鱼香肉丝"));
        storage.add(new Dish("103", "红烧肉"));
        System.out.println("添加后菜品数量：" + storage.size());
        Dish item = storage.get("102");
        System.out.println("查询 id=102 的菜品：" + (item != null ? item.getName() : "null"));
        storage.remove("102");
        System.out.println("删除后再次查询 id=102：" + (storage.get("102") != null ? storage.get("102").getName() : "未找到"));
    }
}
