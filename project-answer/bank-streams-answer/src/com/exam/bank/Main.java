package com.exam.bank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Account> set = new HashSet<>();
        set.add(new Account("103", "王五的账户"));
        set.add(new Account("101", "张三的账户"));
        set.add(new Account("102", "李四的账户"));
        set.add(new Account("102", "李四的账户")); // 重复对象
        System.out.println("添加后去重的账户数量：" + set.size());
        List<Account> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Account item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}
