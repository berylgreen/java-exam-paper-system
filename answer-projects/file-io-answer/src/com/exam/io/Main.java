import java.io.*;

class LogBackup {
    public static void backup(String src, String dest) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
        } catch (IOException e) {
            System.out.println("文件备份失败：" + e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        String src = "temp.log";
        String dest = "backup.log";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(src))) {
            writer.write("[INFO] System started\n");
            writer.write("[INFO] User login success\n");
            writer.write("[ERROR] Disk space low\n");
        } catch (IOException e) {
            System.out.println("测试文件创建失败：" + e.getMessage());
            return;
        }

        LogBackup.backup(src, dest);
        System.out.println("文件备份完成。");
    }
}