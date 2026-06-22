package com.exam.hospital;

class PatientRecord {
    private int count = 100;

    // 使用 synchronized 修饰方法，保证整个“判断 + 修改”过程具有原子性
    public synchronized void update() {
        if (count > 0) {
            count--;
            System.out.println(Thread.currentThread().getName() + " 更新后剩余：" + count);
        }
    }

    public int getCount() {
        return count;
    }
}

public class TestPatientRecord {
    public static void main(String[] args) throws InterruptedException {
        PatientRecord record = new PatientRecord();

        Runnable task = () -> {
            for (int i = 0; i < 30; i++) {
                record.update();
            }
        };

        Thread t1 = new Thread(task, "线程1");
        Thread t2 = new Thread(task, "线程2");
        Thread t3 = new Thread(task, "线程3");
        Thread t4 = new Thread(task, "线程4");

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        System.out.println("最终结果：" + record.getCount());
    }
}
```

也可以使用同步代码块实现：

```java
class PatientRecord {
    private int count = 100;

    public void update() {
        synchronized (this) {
            if (count > 0) {
                count--;
            }
        }
    }

    public int getCount() {
        return count;
    }
}
