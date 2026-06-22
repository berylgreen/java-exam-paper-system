package com.exam.rental;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Storage storage = new Storage();
        storage.add(new Vehicle("101", "丰田卡罗拉"));
        storage.add(new Vehicle("102", "本田雅阁"));
        storage.add(new Vehicle("103", "宝马X5"));
        System.out.println("添加后车辆数量：" + storage.size());
        Vehicle item = storage.get("102");
        System.out.println("查询 id=102 的车辆：" + (item != null ? item.getName() : "null"));
        storage.remove("102");
        System.out.println("删除后再次查询 id=102：" + (storage.get("102") != null ? storage.get("102").getName() : "未找到"));
    }
}
