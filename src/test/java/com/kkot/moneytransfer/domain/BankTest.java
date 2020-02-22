package com.kkot.moneytransfer.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankTest {

	private static final AccountId ACCOUNT_ID_1 = AccountId.of("1");
	private static final AccountId ACCOUNT_ID_2 = AccountId.of("2");
	private Bank bank;

	@BeforeEach
	void setUp() {
		bank = new Bank();
	}

	@Test
	void changeBalanceShouldThrowExceptionWhenAccountDoesntExist() {
		try {
			// when
			bank.changeBalance(ACCOUNT_ID_1, 20);

			fail("Exception should be thrown");
		} catch (IllegalStateException e) {
			// then
			// ok
		}
	}

	@Test
	void shouldChangeBalance_whenAccountExists() {
		// given
		bank.createAccount(ACCOUNT_ID_1);

		// when
		bank.changeBalance(ACCOUNT_ID_1, 20);

		// then
		assertEquals(20, bank.getBalance(ACCOUNT_ID_1).get());
	}

	@Test
	void shouldNotChangeBalance_whenTryingToCreateAccountAgain() {
		// given
		bank.createAccount(ACCOUNT_ID_1);
		bank.changeBalance(ACCOUNT_ID_1, 20);

		// when
		bank.createAccount(ACCOUNT_ID_1);

		// then
		assertEquals(20, bank.getBalance(ACCOUNT_ID_1).get());
	}

	@Test
	void shouldTransferMoney_whenBothAccountExistsAndSufficientBalance() {
		bank.createAccount(ACCOUNT_ID_1);
		bank.createAccount(ACCOUNT_ID_2);
		bank.changeBalance(ACCOUNT_ID_1, 200);
		bank.changeBalance(ACCOUNT_ID_2, 0);

		bank.transfer(new Transfer(50, ACCOUNT_ID_2, ACCOUNT_ID_1));

		assertEquals(150, bank.getBalance(ACCOUNT_ID_1).get());
		assertEquals(50, bank.getBalance(ACCOUNT_ID_2).get());
	}
}
