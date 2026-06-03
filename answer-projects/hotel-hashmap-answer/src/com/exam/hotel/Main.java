import java.util.HashMap;
import java.util.Map;

class Room {
    private String id;
    private String type;

    public Room(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Room{id='" + id + "', type='" + type + "'}";
    }
}

class RoomManager {
    private Map<String, Room> roomMap = new HashMap<>();

    // 添加房间
    public void addRoom(Room room) {
        roomMap.put(room.getId(), room);
    }

    // 根据 ID 获取房间
    public Room getRoomById(String id) {
        return roomMap.get(id);
    }

    // 根据 ID 删除房间
    public void removeRoomById(String id) {
        roomMap.remove(id);
    }
}

public class Main {
    public static void main(String[] args) {
        RoomManager manager = new RoomManager();

        manager.addRoom(new Room("101", "单人间"));
        manager.addRoom(new Room("102", "双人间"));
        manager.addRoom(new Room("103", "豪华间"));

        System.out.println("查询 102 房间：" + manager.getRoomById("102"));

        manager.removeRoomById("102");
        System.out.println("删除后再次查询 102 房间：" + manager.getRoomById("102"));
    }
}