package com.kkot.moneytransfer.domain.status;

import com.kkot.moneytransfer.domain.valueobject.AccountId;

public class AccountIsMissingStatus implements OperationStatus {
    private final AccountId accountId;

    public AccountIsMissingStatus(final AccountId accountId) {
        this.accountId = accountId;
    }

    public Object getAccountId() {
        return this.accountId;
    }
}
