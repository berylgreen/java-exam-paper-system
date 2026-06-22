package com.exam.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Account implements Comparable<Account> {
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
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Account other) {
        return this.id.compareTo(other.id);
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
