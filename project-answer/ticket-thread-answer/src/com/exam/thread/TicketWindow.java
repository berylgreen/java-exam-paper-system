package com.exam.thread;

public class TicketWindow implements Runnable {
    private int tickets = 100;

    @Override
    public void run() {
        while (true) {
            // FIXME: 以下售票逻辑非线程安全
            if (tickets > 0) {
                try {
                    // 模拟网络延迟，放大并发问题
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " 卖出了第 " + tickets + " 张票");
                tickets--;
            } else {
                break;
            }
        }
    }
}
