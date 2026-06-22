package com.exam.library;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Book> set = new HashSet<>();
        set.add(new Book("103", "计算机网络"));
        set.add(new Book("101", "Java编程思想"));
        set.add(new Book("102", "算法导论"));
        set.add(new Book("102", "算法导论")); // 重复对象
        System.out.println("添加后去重的图书数量：" + set.size());
        List<Book> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Book item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}
