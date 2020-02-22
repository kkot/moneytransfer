package com.kkot.moneytransfer.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankTreadingTest {

	private static final AccountId ACCOUNT_ID_1 = AccountId.of("1");
	private static final AccountId ACCOUNT_ID_2 = AccountId.of("2");
	private Bank bank;

	@BeforeEach
	void setUp() {
		bank = new Bank();
	}

	private CountDownLatch spawnThreads(int threadsCount, int repeats,
			final Runnable action) {
		final CountDownLatch finished = new CountDownLatch(threadsCount * repeats);
		ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
		for(int i = 0; i < threadsCount; i++) {
			executorService.execute(() -> {
				for(int j = 0; j < repeats; j++) {
					try {
						action.run();
					} finally {
						finished.countDown();
					}
				}
			});
		}
		return finished;
	}

	@Test
	void shouldTransferMoney_whenBothAccountExistsAndSufficientBalance() throws InterruptedException {
		bank.createAccount(ACCOUNT_ID_1);
		bank.createAccount(ACCOUNT_ID_2);
		bank.changeBalance(ACCOUNT_ID_1, 100_000);
		bank.changeBalance(ACCOUNT_ID_2, 100_000);

		// when
		CountDownLatch to2 = spawnThreads(5, 10_000, () -> {
			bank.transfer(new Transfer(1, ACCOUNT_ID_1, ACCOUNT_ID_2));
		});
		CountDownLatch to1 = spawnThreads(5, 10_000, () -> {
			bank.transfer(new Transfer(1, ACCOUNT_ID_2, ACCOUNT_ID_1));
		});
		to1.await();
		to2.await();

		// then
		assertEquals(100_000, bank.getBalance(ACCOUNT_ID_1).get());
		assertEquals(100_000, bank.getBalance(ACCOUNT_ID_2).get());
	}
}
