package com.exam.bank;

import java.util.HashMap;
import java.util.Map;

class Account {
    private String id;
    private String name;
    private double balance;

    public Account(String id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Account{id='" + id + "', name='" + name + "', balance=" + balance + "}";
    }
}

class BankSystem {
    private Map<String, Account> accountMap = new HashMap<>();

    public void addAccount(Account account) {
        accountMap.put(account.getId(), account);
    }

    public Account getAccountById(String id) {
        return accountMap.get(id);
    }

    public void removeAccountById(String id) {
        accountMap.remove(id);
    }
}

public class Main {
    public static void main(String[] args) {
        BankSystem bankSystem = new BankSystem();

        bankSystem.addAccount(new Account("1001", "Alice", 5000.0));
        bankSystem.addAccount(new Account("1002", "Bob", 3200.0));
        bankSystem.addAccount(new Account("1003", "Charlie", 7800.0));

        System.out.println("查询 ID=1002 的账户：");
        System.out.println(bankSystem.getAccountById("1002"));

        System.out.println("删除 ID=1002 的账户");
        bankSystem.removeAccountById("1002");

        System.out.println("再次查询 ID=1002 的账户：");
        System.out.println(bankSystem.getAccountById("1002"));
    }
}