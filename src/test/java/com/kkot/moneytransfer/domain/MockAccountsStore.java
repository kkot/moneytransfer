package com.kkot.moneytransfer.domain;

import io.quarkus.test.Mock;

import javax.enterprise.context.ApplicationScoped;

@Mock
@ApplicationScoped
public class MockAccountsStore extends AccountsStore {
    public void deleteAccounts() {
        accounts.clear();
    }
}
