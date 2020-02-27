package com.kkot.moneytransfer.api.service;

import com.kkot.moneytransfer.api.dto.AccountDto;
import com.kkot.moneytransfer.api.dto.ErrorDto;
import com.kkot.moneytransfer.domain.AccountsStore;
import com.kkot.moneytransfer.domain.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountApiServiceTest {
    private AccountApiService accountApiService;

    @BeforeEach
    void setUp() {
        accountApiService = new AccountApiService(new Bank(new AccountsStore()));
    }

    @Test
    void createAccountShouldReturnBadRequestWhenIdInUrlIsDifferentThanInBody() {
        // when
        Response account = accountApiService.createAccount(
                "abc", new AccountDto("def", 123));

        // then
        assertEquals(400, account.getStatus());
        assertEquals(new ErrorDto("account id in path is different than in body"), account.getEntity());
    }
}
