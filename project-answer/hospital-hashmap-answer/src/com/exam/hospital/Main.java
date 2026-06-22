package com.exam.hospital;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Storage storage = new Storage();
        storage.add(new Patient("101", "张三的病历"));
        storage.add(new Patient("102", "李四的病历"));
        storage.add(new Patient("103", "王五的病历"));
        System.out.println("添加后病历数量：" + storage.size());
        Patient item = storage.get("102");
        System.out.println("查询 id=102 的病历：" + (item != null ? item.getName() : "null"));
        storage.remove("102");
        System.out.println("删除后再次查询 id=102：" + (storage.get("102") != null ? storage.get("102").getName() : "null"));
    }
}
