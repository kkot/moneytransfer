package com.kkot.moneytransfer.domain;

import java.util.Objects;

public class AccountId {
	private String id;

	private AccountId(String id) {
		this.id = id;
	}

	public static AccountId of(String id) {
		Objects.requireNonNull(id, "id cannot be null");
		return new AccountId(id);
	}

	public String getId() {
		return id;
	}
}
