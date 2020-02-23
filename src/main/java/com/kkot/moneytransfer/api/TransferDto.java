package com.kkot.moneytransfer.api;

import com.kkot.moneytransfer.domain.AccountId;
import com.kkot.moneytransfer.domain.Transfer;

public class TransferDto {
	private String sourceAccountId;
	private String targetAccountId;
	private int amount;

	public TransferDto() {
	}

	public TransferDto(final String sourceAccountId, final String targetAccountId, final int amount) {
		this.sourceAccountId = sourceAccountId;
		this.targetAccountId = targetAccountId;
		this.amount = amount;
	}

	public Transfer toTransfer() {
		return new Transfer(AccountId.of(sourceAccountId), AccountId.of(targetAccountId), amount);
	}

	public String getSourceAccountId() {
		return sourceAccountId;
	}

	public String getTargetAccountId() {
		return targetAccountId;
	}

	public int getAmount() {
		return amount;
	}

	public void setSourceAccountId(final String sourceAccountId) {
		this.sourceAccountId = sourceAccountId;
	}

	public void setTargetAccountId(final String targetAccountId) {
		this.targetAccountId = targetAccountId;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}
}
