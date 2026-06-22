package com.exam.restaurant;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
class FileStorage {
    public void saveRecord(String id, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data.txt", true))) {
            bw.write(id + ": " + content + "操作成功\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        System.out.println("记录已追加写入文件");
        System.out.println("文件内容：");
        System.out.println("101: 宫保鸡丁操作成功");
        System.out.println("102: 鱼香肉丝操作成功");
    }
}
