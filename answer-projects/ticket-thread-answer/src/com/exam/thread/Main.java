package com.exam.thread;

public class Main {
    public static void main(String[] args) {
        TicketWindow task = new TicketWindow();
        
        Thread window1 = new Thread(task, "窗口1");
        Thread window2 = new Thread(task, "窗口2");
        Thread window3 = new Thread(task, "窗口3");
        
        window1.start();
        window2.start();
        window3.start();
    }
}
