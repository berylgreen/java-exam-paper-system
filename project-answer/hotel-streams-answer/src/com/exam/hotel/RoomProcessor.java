package com.exam.hotel;

import java.util.List;
import java.util.stream.Collectors;

class Room {
    private int value;
    private String id;
    public Room() {}
    public Room(boolean valid, String name) { this.valid = valid; this.name = name; }
    public void setValid(boolean valid) { this.valid = valid; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }


    

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