package com.exam.thread;

public class TicketWindow implements Runnable {
    private int tickets = 100;

    @Override
    public void run() {
        while (sellTicket()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private synchronized boolean sellTicket() {
        if (tickets <= 0) {
            return false;
        }
        System.out.println(Thread.currentThread().getName() + " sold ticket: " + tickets);
        tickets--;
        return true;
    }
}