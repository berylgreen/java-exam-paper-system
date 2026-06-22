package com.exam.ecommerce;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ecommerce 模块测试...");
        FileStorage storage = new FileStorage();
        storage.saveRecord("101", "笔记本电脑操作成功");
        storage.saveRecord("102", "智能手机操作成功");
        
        // TODO: 完善 FileStorage.java，使用 FileWriter 和 BufferedWriter 将记录追加写入文件
    }
}
