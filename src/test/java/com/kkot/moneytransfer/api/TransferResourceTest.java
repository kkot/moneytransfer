package com.kkot.moneytransfer.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.kkot.moneytransfer.domain.AccountId;
import com.kkot.moneytransfer.domain.Bank;
import com.kkot.moneytransfer.domain.MockAccountsStore;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class TransferResourceTest {

	@Inject
	private Bank bank;

	@Inject
	private MockAccountsStore mockAccountsStore;

	private static final AccountId ACCOUNT_ID_1 = AccountId.of("1");

	private static final AccountId ACCOUNT_ID_2 = AccountId.of("2");

	@Test
	void shouldReturn400AndReasonWhenSourceAccountDoesNotExist() {
		// given
		mockAccountsStore.deleteAccounts();
		TransferDto transfer = new TransferDto("1", "2", 50);
		given()
				.contentType(ContentType.JSON)
				.body(transfer)
				// when
				.post("/transfer")
				//then
				.then()
				.statusCode(Status.BAD_REQUEST.getStatusCode())
				.body("errorCode", equalTo(TransferErrorType.ACCOUNT_ID_MISSING.getCode()))
				.body("message", equalTo("Account ID '1' is missing"));
	}

	@Test
	public void shouldTransferWhenBalanceIsSufficient() {
		// given
		bank.createAccount(ACCOUNT_ID_1);
		bank.createAccount(ACCOUNT_ID_2);
		bank.changeBalance(ACCOUNT_ID_1, 200);
		bank.changeBalance(ACCOUNT_ID_2, 50);

		TransferDto transfer = new TransferDto("1", "2", 50);

		given()
				.contentType(ContentType.JSON)
				.body(transfer)
				// when
				.post("/transfer")
				// then
				.then()
				.statusCode(Status.OK.getStatusCode());

		assertEquals(150, bank.getBalance(ACCOUNT_ID_1));
		assertEquals(100, bank.getBalance(ACCOUNT_ID_2));
	}

}
