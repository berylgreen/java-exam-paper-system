package com.exam.hospital;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Patient implements Comparable<Patient> {
    private boolean valid;
    private int value;
    public Patient() {}
    public Patient(boolean valid, String name) { this.valid = valid; this.name = name; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public void setId(String id) { this.id = id; }


    private String id;
    private String name;

    public Patient(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Patient other) {
        return this.getId().compareTo(other.getId());
    }

    @Override
    public String toString() {
        return "Patient{id='" + id + "', name='" + name + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Patient> patientSet = new HashSet<>();

        patientSet.add(new Patient("P002", "李四"));
        patientSet.add(new Patient("P001", "张三"));
        patientSet.add(new Patient("P003", "王五"));
        patientSet.add(new Patient("P002", "李四-重复数据"));

        List<Patient> patientList = new ArrayList<>(patientSet);
        Collections.sort(patientList);

        for (Patient patient : patientList) {
            System.out.println(patient);
        }
    }
}