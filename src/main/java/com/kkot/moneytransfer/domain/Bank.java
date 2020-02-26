package com.kkot.moneytransfer.domain;

import com.kkot.moneytransfer.domain.status.AccountIsMissingStatus;
import com.kkot.moneytransfer.domain.status.InsufficientBalanceStatus;
import com.kkot.moneytransfer.domain.status.OkStatus;
import com.kkot.moneytransfer.domain.status.OperationStatus;
import com.kkot.moneytransfer.domain.util.ValueHolder;
import com.kkot.moneytransfer.domain.valueobject.AccountId;
import com.kkot.moneytransfer.domain.valueobject.Transfer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

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

    /**
     * Creates account with given {@code accountId}. It initializes balance to 0.
     *
     * @param accountId account id to create
     */
    public void createAccount(AccountId accountId) {
        this.accountsStore.createAccount(accountId);
    }

    /**
     * Change the balance of account with given id by given amount.
     * This method is thread-safe.
     *
     * @param accountId account id
     * @param amount    amount to change balance, could be negative
     * @return status of the operation
     */
    public OperationStatus changeBalance(AccountId accountId, int amount) {
        boolean accountExisted = accountsStore.accessExclusively(accountId, account ->
                account.changeBalance(amount));
        return accountExisted ? new OkStatus() : new AccountIsMissingStatus(accountId);
    }

    public OperationStatus setBalance(AccountId accountId, Integer balance) {
        boolean accountExisted = accountsStore.accessExclusively(accountId, account ->
                account.setBalance(balance));
        return accountExisted ? new OkStatus() : new AccountIsMissingStatus(accountId);
    }

    /**
     * Returns balance for given {@code accountId}. If the account doesn't exist then 0 is returned.
     * This method is thread-safe.
     *
     * @param accountId account id to check
     * @return balance of the account or 0 if the account with given id doesn't exist
     */
    public int getBalance(AccountId accountId) {
        ValueHolder<Integer> holder = new ValueHolder<>(0);
        accountsStore.accessShared(accountId, account -> holder.setValue(account.getBalance()));
        return holder.getValue();
    }

    /**
     * Checks if an account exists.
     *
     * @param accountId account id to check
     * @return true if exists, false otherwise
     */
    public boolean exists(AccountId accountId) {
        return accountsStore.exists(accountId);
    }

    /**
     * Performs transfer if source and target account exist and source account has sufficient balance.
     * This method is thread-safe.
     *
     * @param transfer to perform
     * @return operation status
     */
    public OperationStatus transfer(final Transfer transfer) {
        ValueHolder<OperationStatus> result = new ValueHolder<>(new OkStatus());

        if (!accountsStore.exists(transfer.getSourceId())) {
            return new AccountIsMissingStatus(transfer.getSourceId());
        }
        if (!accountsStore.exists(transfer.getTargetId())) {
            return new AccountIsMissingStatus(transfer.getTargetId());
        }

        var sourceAndTargetIds = List.of(transfer.getSourceId(), transfer.getTargetId());
        accountsStore.accessExclusively(sourceAndTargetIds, accounts -> {
            Account source = accounts.get(0);
            Account target = accounts.get(1);

            if (source.getBalance() < transfer.getAmount()) {
                result.setValue(new InsufficientBalanceStatus(source.getId()));
                return;
            }
            source.changeBalance(-transfer.getAmount());
            target.changeBalance(transfer.getAmount());
            result.setValue(new OkStatus());
        });

        return result.getValue();
    }
}
