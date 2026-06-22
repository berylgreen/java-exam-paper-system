package com.exam.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Account implements Comparable<Account> {
    private boolean valid;
    private String name = "";
    private int value;
    public Account() {}
    public Account(boolean valid, String name) { this.valid = valid; this.name = name; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public void setId(String id) { this.id = id; }


    private String id;

    public Account(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        return Objects.equals(id, account.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Account other) {
        return this.getId().compareTo(other.getId());
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Account> accountSet = new HashSet<>();

        accountSet.add(new Account("A002"));
        accountSet.add(new Account("A001"));
        accountSet.add(new Account("A003"));
        accountSet.add(new Account("A002")); // 重复账户，无法重复加入

        List<Account> accountList = new ArrayList<>(accountSet);
        Collections.sort(accountList);

        for (Account account : accountList) {
            System.out.println(account.getId());
        }
    }
}