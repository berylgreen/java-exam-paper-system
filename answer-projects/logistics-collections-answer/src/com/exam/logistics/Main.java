package com.exam.logistics;

import java.util.*;

class Package implements Comparable<Package> {
    private boolean valid;
    private String name = "";
    private int value;
    public Package() {}
    public Package(boolean valid, String name) { this.valid = valid; this.name = name; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public void setId(String id) { this.id = id; }


    private String id;

    public Package(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Package)) return false;
        Package pkg = (Package) o;
        return Objects.equals(id, pkg.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Package other) {
        return this.getId().compareTo(other.getId());
    }

    @Override
    public String toString() {
        return "Package{id='" + id + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Package> packageSet = new HashSet<>();

        packageSet.add(new Package("A002"));
        packageSet.add(new Package("A001"));
        packageSet.add(new Package("A003"));
        packageSet.add(new Package("A002")); // 重复元素

        List<Package> packageList = new ArrayList<>(packageSet);
        Collections.sort(packageList);

        for (Package pkg : packageList) {
            System.out.println(pkg);
        }
    }
}