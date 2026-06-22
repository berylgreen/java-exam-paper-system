package com.exam.school;

class StudentRecord {
    private int score = 100;

    // 对共享数据的修改使用 synchronized 进行同步
    public synchronized void updateScore() {
        if (score > 0) {
            score--;
            System.out.println(Thread.currentThread().getName() + " 更新后分数：" + score);
        }
    }

    public int getScore() {
        return score;
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        StudentRecord record = new StudentRecord();

        Runnable task = () -> {
            for (int i = 0; i < 30; i++) {
                record.updateScore();
            }
        };

        Thread t1 = new Thread(task, "线程1");
        Thread t2 = new Thread(task, "线程2");
        Thread t3 = new Thread(task, "线程3");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("最终分数：" + record.getScore());
    }
}