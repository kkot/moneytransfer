package com.kkot.moneytransfer.domain;

import com.kkot.moneytransfer.domain.util.AccessMode;
import com.kkot.moneytransfer.domain.util.ExclusiveAccessMode;
import com.kkot.moneytransfer.domain.util.SharedAccessMode;
import com.kkot.moneytransfer.domain.valueobject.AccountId;
import com.kkot.moneytransfer.domain.valueobject.AccountWithLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Stores @{link Account}s in memory and allow to access them in a thread-safe way.
 */
@ApplicationScoped
public class AccountsStore {
    private static final Logger log = LoggerFactory.getLogger(Bank.class);

    protected Map<AccountId, AccountWithLock> accounts;

    public AccountsStore() {
        this.accounts = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * Creates an account with given id if it doesn't exist.
     *
     * @param accountId account id to create
     */
    public void createAccount(AccountId accountId) {
        log.debug("creating account {}", accountId);
        this.accounts.putIfAbsent(accountId, new AccountWithLock(new Account(accountId)));
        log.debug("account created {}", accountId);
    }

    /**
     * Allows to execute action on {@link Account}s with given Ids having exclusive access.
     * The accounts can be modified inside the operation.
     *
     * @param accountIds account ids to execute operation on, if any account with given id doesn't exist,
     *                   operation is not executed at all
     * @return true if all accounts with ids existed and operation was executed, false otherwise
     */
    public boolean accessExclusively(List<AccountId> accountIds, Consumer<List<Account>> operation) {
        return accessAccounts(accountIds, ExclusiveAccessMode.INSTANCE, operation);
    }

    /**
     * Allows to execute action on {@link Account} with given Id having exclusive access.
     * The account can be modified inside the operation.
     *
     * @param accountId account id to execute operation on, if the account with given id doesn't exist,
     *                  operation is not executed at all
     * @return true account with id existed and operation was executed, false otherwise
     */
    public boolean accessExclusively(AccountId accountId, Consumer<Account> operation) {
        return accessAccounts(List.of(accountId), ExclusiveAccessMode.INSTANCE,
                consumeOneElement(operation));
    }

    /**
     * Allows to execute action on {@link Account} with given Id having shared access.
     * <p>
     * The account must not be modified inside the action because it would lead to concurrency issues.
     * If needed use methods that give exclusive access.
     *
     * @param accountId account id to execute operation on, if the account with given id doesn't exist,
     *                  operation is not executed at all
     * @return true account with id existed and operation was executed, false otherwise
     */
    public boolean accessShared(AccountId accountId, Consumer<Account> operation) {
        return accessAccounts(List.of(accountId), SharedAccessMode.INSTANCE, consumeOneElement(operation));
    }

    /**
     * Allows to execute an action on accounts with given {@code accountIds}.
     * Before executing action locks associated with accounts are acquired
     * in order derived from account ids order so that we avoid deadlock in situation
     * when there is cycle, for example transfer from account A to B and from B to A in
     * the same time.
     * <p>
     * return true if all accounts with given account ids existed and action was executed, false otherwise
     */
    private boolean accessAccounts(
            List<AccountId> accountIds,
            AccessMode accessMode,
            Consumer<List<Account>> action) {
        log.debug("accessing accounts {}, mode {}", accountIds, accessMode);

        Optional<AccountId> firstNotExisting = accountIds
                .stream()
                .filter(id -> !accounts.containsKey(id))
                .findFirst();
        if (firstNotExisting.isPresent()) {
            log.debug("account is missing {}", firstNotExisting.get());
            return false;
        }

        // ids are sorted so that account with lower id is locked first, to avoid deadlocks
        var sortedAccountIds = new TreeSet<>(accountIds);
        List<Lock> sortedLocks = sortedAccountIds
                .stream()
                .map(id -> accounts.get(id).getLock())
                .map(accessMode).collect(Collectors.toList());

        log.debug("before locking accounts {}", sortedAccountIds);
        sortedLocks.forEach(Lock::lock);
        log.debug("accounts locked {}", sortedAccountIds);
        try {
            List<Account> lockedAccounts = accountIds
                    .stream()
                    .map(id -> accounts.get(id))
                    .map(AccountWithLock::getAccount)
                    .collect(Collectors.toList());
            log.debug("invoking action on accounts {}", lockedAccounts);
            action.accept(lockedAccounts);
            log.debug("action finished {}", lockedAccounts);
        } finally {
            log.debug("before unlocking accounts {}", sortedAccountIds);
            sortedLocks.forEach(Lock::unlock);
            log.debug("accounts unlocked {}", sortedAccountIds);
        }
        return true;
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

    /**
     * Checks if an account with given id exists.
     *
     * @return true if exists, false otherwise
     */
    public boolean exists(final AccountId accountId) {
        log.debug("checking if account exists {}", accountId);
        return accounts.containsKey(accountId);
    }
}
