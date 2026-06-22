package com.exam.hospital;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Patient implements Comparable<Patient> {
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
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Patient other) {
        return this.id.compareTo(other.id);
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
