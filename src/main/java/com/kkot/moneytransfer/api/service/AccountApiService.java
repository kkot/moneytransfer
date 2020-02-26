package com.kkot.moneytransfer.api.service;

import com.kkot.moneytransfer.api.dto.AccountDto;
import com.kkot.moneytransfer.api.dto.ErrorDto;
import com.kkot.moneytransfer.domain.Bank;
import com.kkot.moneytransfer.domain.valueobject.AccountId;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class AccountApiService {

    private Bank bank;

    @Inject
    public AccountApiService(final Bank bank) {
        this.bank = bank;
    }

    public Response getAccount(String accountIdStr) {
        if (StringUtils.isBlank(accountIdStr)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        var accountId = AccountId.of(accountIdStr);
        if (!bank.exists(accountId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        int balance = bank.getBalance(accountId);
        return Response.ok().entity(new AccountDto(accountIdStr, balance)).build();
    }

    public Response createAccount(String accountId, AccountDto accountDto) {
        if (StringUtils.isBlank(accountDto.getAccountId())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (!accountDto.getAccountId().equals(accountId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDto("account id in path is different than in body"))
                    .build();
        }
        var id = AccountId.of(accountDto.getAccountId());
        bank.createAccount(id);
        bank.setBalance(id, accountDto.getBalance());
        return Response.ok().build();
    }
}
