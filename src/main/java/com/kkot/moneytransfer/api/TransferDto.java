package com.kkot.moneytransfer.api;

public class TransferDto {
	private String from;
	private String to;
	private int amount;

	public TransferDto() {
	}

	public TransferDto(final String from, final String to, final int amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public int getAmount() {
		return amount;
	}
}
