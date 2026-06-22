package com.exam.school;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 school 模块测试...");
        FileStorage storage = new FileStorage();
        storage.saveRecord("101", "小明操作成功");
        storage.saveRecord("102", "小红操作成功");
        
        // TODO: 完善 FileStorage.java，使用 FileWriter 和 BufferedWriter 将记录追加写入文件
    }
}
