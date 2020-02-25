package com.kkot.moneytransfer.domain.status;

import com.kkot.moneytransfer.domain.valueobject.AccountId;

public class InsufficientBalanceStatus implements OperationStatus {

	private final AccountId accountId;

	public InsufficientBalanceStatus(final AccountId accountId) {
		this.accountId = accountId;
	}

	public AccountId getAccountId() {
		return accountId;
	}
}
