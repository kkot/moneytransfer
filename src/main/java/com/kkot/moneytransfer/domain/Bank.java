package com.kkot.moneytransfer.domain;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.kkot.moneytransfer.domain.status.AccountIsMissingStatus;
import com.kkot.moneytransfer.domain.status.InsufficientBalanceStatus;
import com.kkot.moneytransfer.domain.status.OkStatus;
import com.kkot.moneytransfer.domain.status.OperationStatus;
import com.kkot.moneytransfer.domain.util.ValueHolder;

@ApplicationScoped
public class Bank {

	private AccountsStore accountsStore;

	protected Bank() {
		// needed for CDI
	}

	@Inject
	public Bank(AccountsStore accountsStore) {
		this.accountsStore = accountsStore;
	}

	public void createAccount(AccountId id) {
		this.accountsStore.createAccount(id);
	}

	public OperationStatus changeBalance(AccountId id, int amount) {
		boolean accountExisted = accountsStore.accessExclusively(id, account -> {
			account.changeBalance(amount);
		});
		return accountExisted ? new OkStatus() : new AccountIsMissingStatus(id);
	}

	public int getBalance(AccountId accountId) {
		ValueHolder<Integer> holder = new ValueHolder<>(0);
		accountsStore.accessShared(accountId, account -> holder.setValue(account.getBalance()));
		return holder.getValue();
	}

	public OperationStatus transfer(final Transfer transfer) {
		ValueHolder<OperationStatus> result = new ValueHolder<>(new OkStatus());

		if(!accountsStore.contains(transfer.getSourceId())) {
			return new AccountIsMissingStatus(transfer.getSourceId());
		}
		if(!accountsStore.contains(transfer.getTargetId())) {
			return new AccountIsMissingStatus(transfer.getTargetId());
		}

		List<AccountId> sourceAndTargetIds = List.of(transfer.getSourceId(), transfer.getTargetId());
		accountsStore.accessExclusively(sourceAndTargetIds, accounts -> {
			Account source = accounts.get(0);
			Account target = accounts.get(1);

			if(source.getBalance() < transfer.getAmount()) {
				result.setValue(new InsufficientBalanceStatus());
				return;
			}
			source.changeBalance(-transfer.getAmount());
			target.changeBalance(transfer.getAmount());
			result.setValue(new OkStatus());
		});

		return result.getValue();
	}
}
