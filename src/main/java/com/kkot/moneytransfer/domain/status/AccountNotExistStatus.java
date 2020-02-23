package com.kkot.moneytransfer.domain.status;

import com.kkot.moneytransfer.domain.AccountId;

public class AccountNotExistStatus extends OperationStatus {
	private final AccountId id;

	public AccountNotExistStatus(final AccountId id) {
		this.id = id;
	}

	public Object getId() {
		return this.id;
	}
}
