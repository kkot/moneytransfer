package com.kkot.moneytransfer.api;

enum TransferErrorType {
	ACCOUNT_ID_MISSING(1, "Account ID '%s' is missing"),
	INSUFFICIENT_BALANCE(1, "Account ID '%s' has insufficient balance");

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
