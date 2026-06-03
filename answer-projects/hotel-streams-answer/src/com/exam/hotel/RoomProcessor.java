import java.util.List;
import java.util.stream.Collectors;

class Room {
    private boolean valid;
    private String name;

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return name;
    }
}

public class RoomProcessor {
    public List<String> processList(List<Room> roomList) {
        return roomList.stream()
                .filter(room -> room.isValid())
                .map(room -> room.getName())
                .collect(Collectors.toList());
    }
}