package com.kkot.moneytransfer.domain;

import com.kkot.moneytransfer.domain.status.*;
import com.kkot.moneytransfer.domain.util.ValueHolder;
import com.kkot.moneytransfer.domain.valueobject.AccountId;
import com.kkot.moneytransfer.domain.valueobject.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class Bank {
    private static final Logger log = LoggerFactory.getLogger(Bank.class);

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
        log.info("creating account {}", accountId);
        this.accountsStore.createAccount(accountId);
        log.info("account created {}", accountId);
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
        log.debug("changing balance {}, amount {}", accountId, amount);
        boolean accountExisted = accountsStore.accessExclusively(accountId, account ->
                account.changeBalance(amount));
        log.debug("balanced changed {}", accountExisted);
        return accountExisted ? new OkStatus() : new AccountIsMissingStatus(accountId);
    }

    public OperationStatus setBalance(AccountId accountId, Integer balance) {
        log.debug("setting balance {}, amount {}", accountId, balance);
        boolean accountExisted = accountsStore.accessExclusively(accountId, account ->
                account.setBalance(balance));
        log.debug("balanced set {}", accountExisted);
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
        log.debug("getting balance {}", accountId);
        ValueHolder<Integer> holder = new ValueHolder<>(0);
        accountsStore.accessShared(accountId, account -> {
            holder.setValue(account.getBalance());
        });
        log.debug("balance retrieved {}", accountId);
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
        log.debug("performing transfer {}", transfer);
        ValueHolder<OperationStatus> result = new ValueHolder<>(new OkStatus());

        if (transfer.getAmount() <= 0) {
            return new AmountIsNotPositiveNumberStatus();
        }
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
