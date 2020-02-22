package com.kkot.moneytransfer.domain;

public class Account {
	private AccountId id;

	private int balance;

	public Account(final AccountId id) {
		this.id = id;
		this.balance = 0;
	}

	public void changeBalance(int amount) {
		this.balance += amount;
	}

	public int getBalance() {
		return balance;
	}
}
