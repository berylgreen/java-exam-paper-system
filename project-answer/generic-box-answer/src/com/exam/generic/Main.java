package com.exam.generic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<MyObject> set = new HashSet<>();
        set.add(new MyObject("103", "对象C"));
        set.add(new MyObject("101", "对象A"));
        set.add(new MyObject("102", "对象B"));
        set.add(new MyObject("102", "对象B")); // 重复对象
        System.out.println("添加后去重的对象数量：" + set.size());
        List<MyObject> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (MyObject item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}
