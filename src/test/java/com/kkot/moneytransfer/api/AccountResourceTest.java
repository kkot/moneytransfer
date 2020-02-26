package com.kkot.moneytransfer.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kkot.moneytransfer.api.dto.AccountDto;
import com.kkot.moneytransfer.domain.Bank;
import com.kkot.moneytransfer.domain.MockAccountsStore;
import com.kkot.moneytransfer.domain.valueobject.AccountId;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class AccountResourceTest {
	private static final AccountId ACCOUNT_1 = AccountId.of("1");

	@Inject
	private Bank bank;

	@Inject
	private MockAccountsStore mockAccountsStore;

	@BeforeEach
	void setUp() {
		mockAccountsStore.deleteAccounts();
	}

	@Test
	void shouldAccountIfExists() {
		bank.createAccount(ACCOUNT_1);
		bank.changeBalance(ACCOUNT_1, 123);

		given()
				.when().get("/account/1")
				.then()
				.statusCode(200)
				.body("accountId", is("1"))
				.body("balance", is(123));
		;
	}

	@Test
	void should404whenAccountNotFound() {
		given()
				.when().get("/account/1")
				.then()
				.statusCode(404);
	}

	@Test
	void shouldCreateAccountWithGivenIdAndAmountAndReturn200() {
		// given
		AccountDto accountDto = new AccountDto("abc",55);
		given()
				.contentType(ContentType.JSON)
				.body(accountDto)

				// when
				.put("/account/abc")

				//then
				.then()
				.statusCode(Response.Status.OK.getStatusCode());

		assertEquals(55, bank.getBalance(AccountId.of("abc")));
	}
}
