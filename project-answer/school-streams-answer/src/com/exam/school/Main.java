package com.exam.school;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Student> set = new HashSet<>();
        set.add(new Student("103", "计算机科学"));
        set.add(new Student("101", "高等数学"));
        set.add(new Student("102", "大学物理"));
        set.add(new Student("102", "大学物理")); // 重复对象
        System.out.println("添加后去重的课程数量：" + set.size());
        List<Student> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Student item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}
