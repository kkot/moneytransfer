package com.kkot.moneytransfer.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import com.kkot.moneytransfer.domain.exception.AccountDoesNotExistException;

@ApplicationScoped
public class Bank {

	private Map<AccountId, Account> accounts;

	public Bank() {
		this.accounts = new HashMap<>();
	}

	public void createAccount(AccountId id) {
		this.accounts.putIfAbsent(id, new Account(id));
	}

	public void changeBalance(AccountId id, int amount) {
		Account account = getAccount(id);
		account.changeBalance(amount);
	}

	private Account getAccount(AccountId id) {
		Account account = this.accounts.get(id);
		if (account == null) {
			throw new AccountDoesNotExistException();
		}
		return account;
	}

	public Optional<Integer> getBalance(AccountId accountId) {
		return Optional.ofNullable(accounts.get(accountId))
				.map(Account::getBalance);
	}

	public synchronized void transfer(final Transfer transfer) {
		getAccount(transfer.getFrom()).changeBalance(-transfer.getAmount());
		getAccount(transfer.getTo()).changeBalance(transfer.getAmount());
	}
}
