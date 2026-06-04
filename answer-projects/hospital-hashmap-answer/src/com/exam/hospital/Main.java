package com.exam.hospital;

import java.util.HashMap;
import java.util.Map;

class Patient {
    private String id;
    private String name;
    private int age;

    public Patient(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Patient{id='" + id + "', name='" + name + "', age=" + age + "}";
    }
}

class PatientManager {
    private Map<String, Patient> patientMap = new HashMap<>();

    // 添加患者
    public void addPatient(Patient patient) {
        patientMap.put(patient.getId(), patient);
    }

    // 根据编号查询患者
    public Patient getPatientById(String id) {
        return patientMap.get(id);
    }

    // 根据编号删除患者
    public void removePatientById(String id) {
        patientMap.remove(id);
    }
}

public class Main {
    public static void main(String[] args) {
        PatientManager manager = new PatientManager();

        // 添加患者
        manager.addPatient(new Patient("P001", "张三", 35));
        manager.addPatient(new Patient("P002", "李四", 42));
        manager.addPatient(new Patient("P003", "王五", 28));

        // 根据编号查询患者
        System.out.println("查询 P002: " + manager.getPatientById("P002"));

        // 删除患者
        manager.removePatientById("P002");
        System.out.println("删除后查询 P002: " + manager.getPatientById("P002"));
    }
}