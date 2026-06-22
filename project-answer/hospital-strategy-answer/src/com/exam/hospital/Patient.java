package com.exam.hospital;

public class Patient {
    public String id;
    public String type; // e.g. "TYPE_A", "TYPE_B"
    
    public Patient(String id, String type) {
        this.id = id;
        this.type = type;
    }
}
