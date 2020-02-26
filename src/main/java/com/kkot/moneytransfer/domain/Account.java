package com.kkot.moneytransfer.domain;

import com.kkot.moneytransfer.domain.valueobject.AccountId;

public class Account {
    private final AccountId id;
    private int balance;

    public Account(final AccountId id) {
        this.id = id;
        this.balance = 0;
    }

    public AccountId getId() {
        return id;
    }

    public void changeBalance(int amount) {
        this.balance += amount;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(final Integer balance) {
        this.balance = balance;
    }
}
