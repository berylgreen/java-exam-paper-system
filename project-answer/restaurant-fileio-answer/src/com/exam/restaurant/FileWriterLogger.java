package com.exam.restaurant;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterLogger {
    public void logRecord(String file, String record) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(record);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("写入失败：" + e.getMessage());
        }
    }
}
