package com.kkot.moneytransfer.api;

public class ErrorDto {
	private int code;
	private String message;

	public ErrorDto(final int code, final String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
