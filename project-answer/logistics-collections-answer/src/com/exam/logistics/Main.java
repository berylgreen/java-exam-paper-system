package com.exam.logistics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Package> set = new HashSet<>();
        set.add(new Package("103", "衣物包裹"));
        set.add(new Package("101", "电子产品包裹"));
        set.add(new Package("102", "书籍包裹"));
        set.add(new Package("102", "书籍包裹")); // 重复对象
        System.out.println("添加后去重的包裹数量：" + set.size());
        List<Package> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Package item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}
