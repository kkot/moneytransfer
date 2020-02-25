package com.kkot.moneytransfer.domain.valueobject;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.kkot.moneytransfer.domain.Account;

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
