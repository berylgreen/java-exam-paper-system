package com.exam.ecommerce;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
public class FileStorage {
    public void saveRecord(String id, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data.txt", true))) {
            bw.write(id + ": " + content + "操作成功\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
