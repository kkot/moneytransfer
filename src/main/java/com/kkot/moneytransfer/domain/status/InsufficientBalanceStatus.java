package com.kkot.moneytransfer.domain.status;

import com.kkot.moneytransfer.domain.valueobject.AccountId;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InsufficientBalanceStatus implements OperationStatus {
    private final AccountId accountId;

    public InsufficientBalanceStatus(final AccountId accountId) {
        this.accountId = accountId;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("accountId", accountId)
                .toString();
    }
}
