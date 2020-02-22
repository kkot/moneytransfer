package com.kkot.moneytransfer.domain;

public class Transfer {
	private AccountId from;
	private AccountId to;
	private int amount;

	public Transfer(final int amount, final AccountId to, final AccountId from) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	public AccountId getFrom() {
		return from;
	}

	public AccountId getTo() {
		return to;
	}

	public int getAmount() {
		return amount;
	}
}
