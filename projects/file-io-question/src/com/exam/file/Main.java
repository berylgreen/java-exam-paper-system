package com.exam.file;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 file 模块测试...");
        FileStorage storage = new FileStorage();
        storage.saveRecord("101", "记录1操作成功");
        storage.saveRecord("102", "记录2操作成功");
        
        // TODO: 完善 FileStorage.java，使用 FileWriter 和 BufferedWriter 将记录追加写入文件
    }
}
