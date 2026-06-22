package com.exam.bank;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 银行系统 模块测试...");
        Account target = new Account();
        
        Thread t1 = new Thread(new Worker(target), "Thread-1");
        Thread t2 = new Thread(new Worker(target), "Thread-2");
        Thread t3 = new Thread(new Worker(target), "Thread-3");
        
        t1.start();
        t2.start();
        t3.start();
        
        // TODO: 使用 synchronized 关键字解决多线程竞态条件，保证共享数据一致性
    }
}
