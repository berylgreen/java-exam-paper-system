package com.exam.library;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 library 模块测试...");
        FileStorage storage = new FileStorage();
        storage.saveRecord("101", "Java编程思想操作成功");
        storage.saveRecord("102", "算法导论操作成功");
        
        // TODO: 完善 FileStorage.java，使用 FileWriter 和 BufferedWriter 将记录追加写入文件
    }
}
