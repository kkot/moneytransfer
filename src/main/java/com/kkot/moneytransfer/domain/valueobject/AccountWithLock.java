package com.kkot.moneytransfer.domain.valueobject;

import com.kkot.moneytransfer.domain.Account;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AccountWithLock {
    private final Account account;
    private final ReadWriteLock lock;

    public AccountWithLock(final Account account) {
        this.account = account;
        this.lock = new ReentrantReadWriteLock();
    }

    public Account getAccount() {
        return account;
    }

    public ReadWriteLock getLock() {
        return lock;
    }
}
