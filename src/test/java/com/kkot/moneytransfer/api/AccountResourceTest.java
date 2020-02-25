package com.kkot.moneytransfer.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.kkot.moneytransfer.domain.Bank;
import com.kkot.moneytransfer.domain.MockAccountsStore;
import com.kkot.moneytransfer.domain.valueobject.AccountId;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class AccountResourceTest {
	private static final AccountId ACCOUNT_1 = AccountId.of("1");

	@Inject
	private Bank bank;

	@Inject
	private MockAccountsStore mockAccountsStore;

	@Test
	void shouldReturnBalanceWhenAccountExist() {
		mockAccountsStore.deleteAccounts();
		bank.createAccount(ACCOUNT_1);
		bank.changeBalance(ACCOUNT_1, 123);

		given()
				.when().get("/account/1/balance")
				.then()
				.statusCode(200)
				.body(is("123"));
	}

	@Test
	void shouldReturnBalanceZeroWhenAccountDoesntExist() {
		mockAccountsStore.deleteAccounts();

		given()
				.when().get("/account/1/balance")
				.then()
				.statusCode(200)
				.body(is("0"));
	}

	@Test
	void shouldCreateAccountAndReturn200() {
		// TODO
	}
}
