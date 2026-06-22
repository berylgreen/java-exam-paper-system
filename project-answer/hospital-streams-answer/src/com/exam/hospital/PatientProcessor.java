package com.exam.hospital;

import java.util.List;
import java.util.stream.Collectors;

class Patient {
    private int value;
    private String id;
    public Patient() {}
    public Patient(boolean valid, String name) { this.valid = valid; this.name = name; }
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

public class PatientProcessor {
    public List<String> processList(List<Patient> list) {
        return list.stream()
                .filter(patient -> patient.isValid())
                .map(Patient::getName)
                .collect(Collectors.toList());
    }
}