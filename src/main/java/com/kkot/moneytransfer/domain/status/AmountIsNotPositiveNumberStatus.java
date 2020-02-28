package com.kkot.moneytransfer.domain.status;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class AmountIsNotPositiveNumberStatus implements OperationStatus {

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .toString();
    }
}
