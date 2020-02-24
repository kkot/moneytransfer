package com.kkot.moneytransfer.domain.status;

import com.kkot.moneytransfer.domain.AccountId;

public class AccountIsMissingStatus implements OperationStatus {
	private final AccountId id;

	public AccountIsMissingStatus(final AccountId id) {
		this.id = id;
	}

	public Object getId() {
		return this.id;
	}
}
