package com.kkot.moneytransfer.domain;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class AccountWithLock {
	private Account account;
	private ReadWriteLock lock;

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
