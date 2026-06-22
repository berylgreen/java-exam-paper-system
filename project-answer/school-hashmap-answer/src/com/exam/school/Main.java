package com.exam.school;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Map<String, Student> map = new HashMap<>();
        map.put("101", new Student("101", "高等数学"));
        map.put("102", new Student("102", "大学物理"));
        map.put("103", new Student("103", "计算机科学"));
        System.out.println("添加后课程数量：" + map.size());
        Student item = map.get("102");
        System.out.println("查询 id=102 的课程：" + (item != null ? item.getName() : "null"));
        map.remove("102");
        System.out.println("删除后再次查询 id=102：" + (map.get("102") != null ? map.get("102").getName() : "null"));
    }
}
