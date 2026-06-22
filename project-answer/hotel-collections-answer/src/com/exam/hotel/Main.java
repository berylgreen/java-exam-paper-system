package com.exam.hotel;

import java.util.*;

class Room implements Comparable<Room> {
    private boolean valid;
    private String name = "";
    private int value;
    private String id;
    
    public Room() {}
    public Room(boolean valid, String name) { this.valid = valid; this.name = name; }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    private String roomId;

    public Room(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(roomId, room.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }

    @Override
    public int compareTo(Room other) {
        return this.roomId.compareTo(other.roomId);
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId='" + roomId + '\'' +
                '}';
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Room> roomSet = new HashSet<>();

        roomSet.add(new Room("A002"));
        roomSet.add(new Room("A001"));
        roomSet.add(new Room("A003"));
        roomSet.add(new Room("A002")); // 重复房间，不能重复加入

        List<Room> roomList = new ArrayList<>(roomSet);
        Collections.sort(roomList);

        for (Room room : roomList) {
            System.out.println(room);
        }
    }
}