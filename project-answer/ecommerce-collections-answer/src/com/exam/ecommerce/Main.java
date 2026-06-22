package com.exam.ecommerce;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Order> set = new HashSet<>();
        set.add(new Order("103", "蓝牙耳机"));
        set.add(new Order("101", "笔记本电脑"));
        set.add(new Order("102", "智能手机"));
        set.add(new Order("102", "智能手机")); // 重复对象
        System.out.println("添加后去重的商品数量：" + set.size());
        List<Order> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Order item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}
