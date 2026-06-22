package com.exam.hospital;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Patient> set = new HashSet<>();
        set.add(new Patient("103", "王五的病历"));
        set.add(new Patient("101", "张三的病历"));
        set.add(new Patient("102", "李四的病历"));
        set.add(new Patient("102", "李四的病历")); // 重复对象
        System.out.println("添加后去重的病历数量：" + set.size());
        List<Patient> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Patient item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}
