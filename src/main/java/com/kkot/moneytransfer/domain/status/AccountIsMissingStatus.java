package com.kkot.moneytransfer.domain.status;

import com.kkot.moneytransfer.domain.valueobject.AccountId;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AccountIsMissingStatus implements OperationStatus {
    private final AccountId accountId;

    public AccountIsMissingStatus(final AccountId accountId) {
        this.accountId = accountId;
    }

    public Object getAccountId() {
        return this.accountId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("accountId", accountId)
                .toString();
    }
}
