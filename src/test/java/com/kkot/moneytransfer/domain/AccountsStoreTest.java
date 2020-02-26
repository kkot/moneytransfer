package com.kkot.moneytransfer.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import com.kkot.moneytransfer.domain.valueobject.AccountId;

class AccountsStoreTest {

	public static final AccountId ACCOUNT_ID_1 = AccountId.of("abc1");
	public static final AccountId ACCOUNT_ID_2 = AccountId.of("abc2");

	private AccountsStore accountsStore;
	private MockConsumer mockConsumer;

	@BeforeEach
	void setUp() {
		accountsStore = new AccountsStore();
		mockConsumer = new MockConsumer();
	}

	private static Object[][] provideTwoAccountsInDifferentOrder() {
		return new Object[][] {
				{ACCOUNT_ID_1, ACCOUNT_ID_2},
				{ACCOUNT_ID_2, ACCOUNT_ID_1}};
	}

	@Test
	void accountShouldExistAfterItWasCreated() {
		// when
		boolean existBefore = accountsStore.exists(ACCOUNT_ID_1);
		accountsStore.createAccount(ACCOUNT_ID_1);
		boolean existAfter = accountsStore.exists(ACCOUNT_ID_1);

		// then
		assertFalse(existBefore, "account should not exist");
		assertTrue(existAfter, "account should exist");
	}

	@Test
	void accessExclusivelyShouldReturnFalseAndNotExecuteActionIfAccountDoesntExist() {
		// given
		Consumer<Account> consumer = Mockito.mock(Consumer.class);

		// when
		boolean result = accountsStore.accessExclusively(ACCOUNT_ID_1, consumer);

		// then
		assertFalse(result, "should return false");
		Mockito.verify(consumer, never()).accept(Mockito.any());
	}

	@ParameterizedTest
	@MethodSource("provideTwoAccountsInDifferentOrder")
	void accessExclusivelyShouldReturnTrueAndExecuteActionForManyAccountIfExistInRightOrder(AccountId id1, AccountId id2) {
		// given
		// create is the same order
		accountsStore.createAccount(ACCOUNT_ID_1);
		accountsStore.createAccount(ACCOUNT_ID_2);

		// when
		boolean result = accountsStore.accessExclusively(List.of(id1, id2), mockConsumer);

		// then
		var accounts = mockConsumer.getAccounts();
		assertTrue(result, "result should be true");
		assertEquals(2, accounts.size());
		assertEquals(id1, accounts.get(0).getId());
		assertEquals(id2, accounts.get(1).getId());
	}
}
