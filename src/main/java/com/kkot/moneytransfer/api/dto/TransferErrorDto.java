package com.kkot.moneytransfer.api.dto;

import com.kkot.moneytransfer.api.TransferErrorType;

public class TransferErrorDto {

    private TransferErrorType errorType;
    private Object[] params;

    public TransferErrorDto(TransferErrorType errorType, Object... params) {
        this.errorType = errorType;
        this.params = params;
    }

    public String getMessage() {
        return String.format(errorType.getMessage(), params);
    }

    public int getErrorCode() {
        return errorType.getCode();
    }
}
