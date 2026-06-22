package com.exam.hotel;

abstract class Room {
    // 统一的处理方法，由子类分别实现
    public abstract void process();
}

class RegularRoom extends Room {
    @Override
    public void process() {
        System.out.println("处理普通房间的统计逻辑");
    }
}

class VIPRoom extends Room {
    @Override
    public void process() {
        System.out.println("处理 VIP 房间的统计逻辑");
    }
}

class SuiteRoom extends Room {
    @Override
    public void process() {
        System.out.println("处理套房的统计逻辑");
    }
}

public class RoomStatistics {
    public void processAll(Room[] rooms) {
        for (Room room : rooms) {
            room.process(); // 通过多态调用子类自己的实现
        }
    }

    public static void main(String[] args) {
        Room[] rooms = {
            new RegularRoom(),
            new VIPRoom(),
            new SuiteRoom()
        };

        RoomStatistics statistics = new RoomStatistics();
        statistics.processAll(rooms);
    }
}
