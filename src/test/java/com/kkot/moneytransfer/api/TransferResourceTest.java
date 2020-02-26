package com.kkot.moneytransfer.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kkot.moneytransfer.api.dto.TransferDto;
import com.kkot.moneytransfer.domain.Bank;
import com.kkot.moneytransfer.domain.MockAccountsStore;
import com.kkot.moneytransfer.domain.valueobject.AccountId;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class TransferResourceTest {

	private static final AccountId ACCOUNT_ID_1 = AccountId.of("1");

	private static final AccountId ACCOUNT_ID_2 = AccountId.of("2");

	@Inject
	private Bank bank;

	@Inject
	private MockAccountsStore mockAccountsStore;

	private void initializeAccount(final AccountId accountId, final int amount) {
		bank.createAccount(accountId);
		bank.changeBalance(accountId, amount);
	}

	@BeforeEach
	void setUp() {
		mockAccountsStore.deleteAccounts();
	}

	@Test
	void shouldReturn400AndReasonWhenSourceAccountDoesNotExist() {
		// given
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
		initializeAccount(ACCOUNT_ID_1, 200);
		initializeAccount(ACCOUNT_ID_2, 50);

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

	@Test
	public void shouldNotTransferWhenBalanceIsInsufficient() {
		// given
		initializeAccount(ACCOUNT_ID_1, 50);
		initializeAccount(ACCOUNT_ID_2, 1);

		TransferDto transfer = new TransferDto("1", "2", 51);

		given()
				.contentType(ContentType.JSON)
				.body(transfer)

				// when
				.post("/transfer")

				// then
				.then()
				.statusCode(Status.BAD_REQUEST.getStatusCode())
				.body("errorCode", equalTo(TransferErrorType.INSUFFICIENT_BALANCE.getCode()))
				.body("message", equalTo("Account ID '" + ACCOUNT_ID_1.getId() + "' has insufficient balance"));

		assertEquals(50, bank.getBalance(ACCOUNT_ID_1));
		assertEquals(1, bank.getBalance(ACCOUNT_ID_2));
	}

}
