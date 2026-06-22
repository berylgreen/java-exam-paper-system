package com.exam.hotel;

class RoomManager {
    private int roomCount = 100;

    // 方法级同步
    public synchronized void bookRoom() {
        if (roomCount > 0) {
            roomCount--;
            System.out.println(Thread.currentThread().getName() + " 预订成功，剩余房间：" + roomCount);
        } else {
            System.out.println(Thread.currentThread().getName() + " 预订失败，房间已满");
        }
    }

    public int getRoomCount() {
        return roomCount;
    }
}

public class TestRoomManager {
    public static void main(String[] args) throws InterruptedException {
        RoomManager manager = new RoomManager();

        Runnable task = () -> {
            for (int i = 0; i < 30; i++) {
                manager.bookRoom();
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

        System.out.println("最终剩余房间数：" + manager.getRoomCount());
    }
}
```

也可以使用同步代码块实现：

```java
class RoomManager {
    private int roomCount = 100;

    public void bookRoom() {
        synchronized (this) {
            if (roomCount > 0) {
                roomCount--;
                System.out.println(Thread.currentThread().getName() + " 预订成功，剩余房间：" + roomCount);
            } else {
                System.out.println(Thread.currentThread().getName() + " 预订失败，房间已满");
            }
        }
    }

    public int getRoomCount() {
        return roomCount;
    }
}
