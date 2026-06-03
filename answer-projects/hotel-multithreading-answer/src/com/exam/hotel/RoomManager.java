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