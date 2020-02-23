package com.kkot.moneytransfer.domain;

import java.util.Objects;

public class AccountId implements Comparable<AccountId> {
	private final String id;

	private AccountId(String id) {
		Objects.requireNonNull(id, "id cannot be null");
		this.id = id;
	}

	public static AccountId of(String id) {
		return new AccountId(id);
	}

	public String getId() {
		return id;
	}

	@Override
	public int compareTo(final AccountId o) {
		return this.getId().compareTo(o.getId());
	}

	@Override
	public boolean equals(final Object o) {
		if(this == o)
			return true;
		if(!(o instanceof AccountId))
			return false;

		final AccountId accountId = (AccountId) o;
		return Objects.equals(id, accountId.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return id;
	}
}
