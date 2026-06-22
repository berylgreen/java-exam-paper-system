package com.exam.smarthome;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("启动 smarthome 模块测试...");
        SharedResource resource = new SharedResource(100);
        
        Thread t1 = new Thread(() -> {
            for (int i=0; i<1000; i++) resource.decrement();
        });
        Thread t2 = new Thread(() -> {
            for (int i=0; i<1000; i++) resource.decrement();
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        
        System.out.println("最终数量：" + resource.getCount() + " (预期应为负数或0，但由于竞态条件会导致结果不正确)");
        
        // TODO: 分析并发问题，使用 synchronized 进行同步控制
    }
}
