package com.exam.library;

public class Worker implements Runnable {
    private Book target;
    
    public Worker(Book target) {
        this.target = target;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            // “读取-判断-修改” 分离，存在线程安全问题
            if (target.stock > 0) {
                try {
                    Thread.sleep(5); // 模拟耗时
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                target.stock--;
                System.out.println(Thread.currentThread().getName() + " 操作后，剩余: " + target.stock);
            }
        }
    }
}
