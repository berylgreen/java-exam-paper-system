package com.exam.io;

import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String sourceFile = "server.log";
        String backupFile = "server_backup.log";
        
        // 模拟生成日志文件
        try (FileWriter fw = new FileWriter(sourceFile)) {
            fw.write("2026-05-31 INFO System started.\n");
            fw.write("2026-05-31 ERROR Out of memory.\n");
        }
        
        LogBackupTool tool = new LogBackupTool();
        tool.backupLog(sourceFile, backupFile);
        
        System.out.println("备份完成，请检查是否生成了 " + backupFile);
    }
}
