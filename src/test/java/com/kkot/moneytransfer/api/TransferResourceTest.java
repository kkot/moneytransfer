package com.kkot.moneytransfer.api;

import com.kkot.moneytransfer.api.dto.TransferDto;
import com.kkot.moneytransfer.domain.Bank;
import com.kkot.moneytransfer.domain.MockAccountsStore;
import com.kkot.moneytransfer.domain.valueobject.AccountId;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class TransferResourceTest {

	private static final String PATH_TRANSFERS = "/transfers";
	private static final AccountId ACCOUNT_ID_1 = AccountId.of("1");
	private static final AccountId ACCOUNT_ID_2 = AccountId.of("2");
	public static final int ACCOUNT_1_BALANCE = 200;
	public static final int ACCOUNT_2_BALANCE = 50;

	@Inject
	private Bank bank;

	@Inject
	private MockAccountsStore mockAccountsStore;

	private void initializeAccount(final AccountId accountId, final int amount) {
		bank.createAccount(accountId);
		bank.changeBalance(accountId, amount);
	}

	private void initializeTestAccounts() {
		initializeAccount(ACCOUNT_ID_1, ACCOUNT_1_BALANCE);
		initializeAccount(ACCOUNT_ID_2, ACCOUNT_2_BALANCE);
	}

	private void assertTestAccountsBalancesNotChanged() {
		assertEquals(ACCOUNT_1_BALANCE, bank.getBalance(ACCOUNT_ID_1));
		assertEquals(ACCOUNT_2_BALANCE, bank.getBalance(ACCOUNT_ID_2));
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
				.post(PATH_TRANSFERS)

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
				.post(PATH_TRANSFERS)

				// then
				.then()
				.statusCode(Status.OK.getStatusCode());

		assertEquals(150, bank.getBalance(ACCOUNT_ID_1));
		assertEquals(100, bank.getBalance(ACCOUNT_ID_2));
	}

	@Test
	public void shouldNotTransferWhenBalanceIsInsufficient() {
		// given
        initializeTestAccounts();

		TransferDto transfer = new TransferDto("1", "2", ACCOUNT_1_BALANCE + 1);

		given()
				.contentType(ContentType.JSON)
				.body(transfer)

				// when
				.post(PATH_TRANSFERS)

				// then
				.then()
				.statusCode(Status.BAD_REQUEST.getStatusCode())
				.body("errorCode", equalTo(TransferErrorType.INSUFFICIENT_BALANCE.getCode()))
				.body("message", equalTo("Account ID '" + ACCOUNT_ID_1.getId() + "' has insufficient balance"));

		assertTestAccountsBalancesNotChanged();
	}

	@Test
	public void shouldNotTransferAmountIsNegative() {
		// given
		initializeTestAccounts();

		TransferDto transfer = new TransferDto("1", "2", -50);
		given()
				.contentType(ContentType.JSON)
				.body(transfer)

				// when
				.post(PATH_TRANSFERS)

				// then
				.then()
				.statusCode(Status.BAD_REQUEST.getStatusCode())
				.body("errorCode", equalTo(TransferErrorType.INSUFFICIENT_BALANCE.getCode()))
				.body("message", equalTo("Amount to transfer must be greater than 0"));

		assertTestAccountsBalancesNotChanged();
	}
}
