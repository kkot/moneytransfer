package com.kkot.moneytransfer.api.dto;

public class AccountDto {
    private String accountId;
    private Integer balance;

    public AccountDto() {
    }

    public AccountDto(final String accountId, final Integer balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(final Integer balance) {
        this.balance = balance;
    }
}
