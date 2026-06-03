package com.exam.hospital;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 医疗系统 模块测试...");
        Storage storage = new Storage();
        storage.add("001", new Patient("001", "Info 1"));
        
        Patient item = storage.get("001");
        System.out.println("获取到: " + (item != null ? item.info : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
