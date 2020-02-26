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

/**
 * Stores @{link Account}s in memory and allow to access them in a thread-safe way.
 */
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
				consumeOneElement(operation));
	}

	/**
	 * Creates a consumer that consumes the the first element of list.
	 * The list must have single element or {@link IllegalArgumentException} is thrown.
	 */
	private Consumer<List<Account>> consumeOneElement(final Consumer<Account> operation) {
		return accountsToConsume -> {
			if (accountsToConsume.size() != 1) {
				throw new IllegalArgumentException("list of accounts should have single element");
			}
			operation.accept(accountsToConsume.get(0));
		};
	}

	public boolean accessShared(AccountId accountId, Consumer<Account> operation) {
		return accessAccounts(List.of(accountId), ReadWriteLock::readLock, consumeOneElement(operation));
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

	public boolean exists(final AccountId sourceId) {
		return accounts.containsKey(sourceId);
	}
}
