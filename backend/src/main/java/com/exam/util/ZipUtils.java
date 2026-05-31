package com.exam.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    /**
     * 将指定目录打成 ZIP 包并返回字节数组
     */
    public static byte[] zipDirectoryToBytes(String sourceDirPath) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            // 解析相对路径：假设从 backend 目录启动，则需要向上找一层
            Path base = Paths.get(System.getProperty("user.dir"));
            if (base.getFileName() != null && base.getFileName().toString().equals("backend")) {
                base = base.getParent();
            }
            Path sourcePath = base.resolve(sourceDirPath).normalize();

            if (!Files.exists(sourcePath) || !Files.isDirectory(sourcePath)) {
                throw new IOException("指定的项目路径不存在或不是目录: " + sourcePath.toAbsolutePath().toString());
            }
            zipFile(sourcePath.toFile(), sourcePath.toFile().getName(), zos);
        }
        return baos.toByteArray();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zos.putNextEntry(new ZipEntry(fileName));
                zos.closeEntry();
            } else {
                zos.putNextEntry(new ZipEntry(fileName + "/"));
                zos.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zos);
                }
            }
            return;
        }

        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
        }
    }
}
