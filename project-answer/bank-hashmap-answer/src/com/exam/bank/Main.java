package com.exam.bank;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Storage storage = new Storage();
        storage.add(new Account("101", "张三的账户"));
        storage.add(new Account("102", "李四的账户"));
        storage.add(new Account("103", "王五的账户"));
        System.out.println("添加后账户数量：" + storage.size());
        Account item = storage.get("102");
        System.out.println("查询 id=102 的账户：" + (item != null ? item.getName() : "null"));
        storage.remove("102");
        System.out.println("删除后再次查询 id=102：" + (storage.get("102") != null ? storage.get("102").getName() : "未找到"));
    }
}
