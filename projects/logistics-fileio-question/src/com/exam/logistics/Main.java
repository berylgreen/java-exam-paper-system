package com.exam.logistics;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 logistics 模块测试...");
        FileStorage storage = new FileStorage();
        storage.saveRecord("101", "电子产品包裹操作成功");
        storage.saveRecord("102", "书籍包裹操作成功");
        
        // TODO: 完善 FileStorage.java，使用 FileWriter 和 BufferedWriter 将记录追加写入文件
    }
}
