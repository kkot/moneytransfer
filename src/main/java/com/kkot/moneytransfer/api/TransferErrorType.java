package com.kkot.moneytransfer.api;

public enum TransferErrorType {
    ACCOUNT_ID_MISSING(1, "Account ID '%s' is missing"),
    INSUFFICIENT_BALANCE(2, "Account ID '%s' has insufficient balance"),
    NOT_POSITIVE_AMOUNT(2, "Amount to transfer must be greater than 0");

    private int code;
    private String message;

    TransferErrorType(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
