package com.kkot.moneytransfer.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import com.kkot.moneytransfer.domain.valueobject.AccountId;
import com.kkot.moneytransfer.domain.valueobject.AccountWithLock;

@ApplicationScoped
class AccountsStore {
	protected Map<AccountId, AccountWithLock> accounts;

	public AccountsStore() {
		this.accounts = Collections.synchronizedMap(new HashMap<>());
	}

	public void createAccount(AccountId accountId) {
		this.accounts.putIfAbsent(accountId, new AccountWithLock(new Account(accountId)));
	}

	public boolean accessExclusively(List<AccountId> accountIds, Consumer<List<Account>> operation) {
		return accessAccounts(accountIds, ReadWriteLock::writeLock, operation);
	}

	public boolean accessExclusively(AccountId accountId, Consumer<Account> operation) {
		return accessAccounts(List.of(accountId), ReadWriteLock::writeLock,
				(accounts -> operation.accept(accounts.get(0))));
	}

	public boolean accessShared(AccountId accountId, Consumer<Account> operation) {
		return accessAccounts(List.of(accountId), ReadWriteLock::readLock,
				(accounts -> operation.accept(accounts.get(0))));
	}

	private boolean accessAccounts(
			List<AccountId> accountIds,
			Function<ReadWriteLock, Lock> getLock,
			Consumer<List<Account>> action) {

		Optional<AccountId> firstNotExisting = accountIds
				.stream()
				.filter(id -> !accounts.containsKey(id))
				.findFirst();
		if (firstNotExisting.isPresent()) {
			return false;
		}

		// ids are sorted so that account with lower id is locked first, to avoid deadlocks
		SortedSet<AccountId> sortedIds = new TreeSet<>(accountIds);
		List<Lock> sortedLocks = sortedIds
				.stream()
				.map(id -> accounts.get(id).getLock())
				.map(getLock).collect(Collectors.toList());

		sortedLocks.forEach(Lock::lock);
		try {
			List<Account> lockedAccounts = accountIds
					.stream()
					.map(id -> accounts.get(id))
					.map(AccountWithLock::getAccount)
					.collect(Collectors.toList());
			action.accept(lockedAccounts);
		}
		finally {
			sortedLocks.forEach(Lock::unlock);
		}
		return true;
	}

	public boolean contains(final AccountId sourceId) {
		return accounts.containsKey(sourceId);
	}
}
