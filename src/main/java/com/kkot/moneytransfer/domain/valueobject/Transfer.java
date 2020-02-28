package com.kkot.moneytransfer.domain.valueobject;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Transfer {
    private final AccountId sourceId;
    private final AccountId targetId;
    private final int amount;

    public Transfer(final AccountId sourceId, final AccountId targetId, final int amount) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.amount = amount;
    }

    public AccountId getSourceId() {
        return sourceId;
    }

    public AccountId getTargetId() {
        return targetId;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sourceId", sourceId)
                .append("targetId", targetId)
                .append("amount", amount)
                .toString();
    }
}
