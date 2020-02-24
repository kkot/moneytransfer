package com.kkot.moneytransfer.domain;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.test.Mock;

@Mock
@ApplicationScoped
public class MockAccountsStore extends AccountsStore {

	public void deleteAccounts() {
		accounts.clear();
	}
}
