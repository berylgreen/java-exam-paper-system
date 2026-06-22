package com.exam.bank;

class BankAccount {
    private int balance = 100;

    // 同步方法：保证同一时刻只有一个线程能修改余额
    public synchronized void withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            System.out.println(Thread.currentThread().getName()
                    + " 取款 " + amount + "，余额：" + balance);
        } else {
            System.out.println(Thread.currentThread().getName()
                    + " 取款失败，余额不足，当前余额：" + balance);
        }
    }

    public int getBalance() {
        return balance;
    }
}

public class BankTest {
    public static void main(String[] args) throws InterruptedException {
        BankAccount account = new BankAccount();

        Runnable task = () -> {
            for (int i = 0; i < 20; i++) {
                account.withdraw(1);
            }
        };

        Thread t1 = new Thread(task, "线程A");
        Thread t2 = new Thread(task, "线程B");
        Thread t3 = new Thread(task, "线程C");
        Thread t4 = new Thread(task, "线程D");

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        System.out.println("最终余额：" + account.getBalance());
    }
}
```

也可以将同步逻辑写成同步代码块：

```java
class BankAccount {
    private int balance = 100;

    public void withdraw(int amount) {
        synchronized (this) {
            if (balance >= amount) {
                balance -= amount;
                System.out.println(Thread.currentThread().getName()
                        + " 取款 " + amount + "，余额：" + balance);
            } else {
                System.out.println(Thread.currentThread().getName()
                        + " 取款失败，余额不足，当前余额：" + balance);
            }
        }
    }

    public int getBalance() {
        return balance;
    }
}
