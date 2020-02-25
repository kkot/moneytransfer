package com.kkot.moneytransfer.domain.valueobject;

public class Transfer {
	private final AccountId sourceId;
	private final AccountId targetId;
	private final int amount;

	public Transfer(final AccountId sourceId, final AccountId targetId, final int amount) {
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.amount = amount;
	}

	public AccountId getSourceId() {
		return sourceId;
	}

	public AccountId getTargetId() {
		return targetId;
	}

	public int getAmount() {
		return amount;
	}
}
