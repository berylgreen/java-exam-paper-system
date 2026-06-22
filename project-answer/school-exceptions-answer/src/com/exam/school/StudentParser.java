package com.exam.school;

class StudentException extends Exception {
    public StudentException(String message) {
        super(message);
    }
}

public class StudentParser {

    public void parseList(String[] data) {
        for (String item : data) {
            try {
                parseStudent(item);
            } catch (StudentException e) {
                System.err.println("学生数据解析失败：" + e.getMessage());
            }
        }
    }

    private void parseStudent(String item) throws StudentException {
        if (item == null || item.trim().isEmpty()) {
            throw new StudentException("数据为空");
        }

        // 示例：此处假设学生数据至少应包含逗号分隔的两个字段
        String[] parts = item.split(",");
        if (parts.length < 2) {
            throw new StudentException("数据格式不正确：" + item);
        }

        System.out.println("解析成功：" + item);
    }
}
