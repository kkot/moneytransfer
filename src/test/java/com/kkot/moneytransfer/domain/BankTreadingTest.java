package com.kkot.moneytransfer.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankTreadingTest {

	private static final AccountId ACCOUNT_ID_1 = AccountId.of("1");
	private static final AccountId ACCOUNT_ID_2 = AccountId.of("2");
	private Bank bank;

	@BeforeEach
	void setUp() {
		bank = new Bank(new AccountsStore());
	}

	/**
	 * Repeats an action by {@code threadsCount} threads, each ({@code repeatsCount}) times.
	 * @param startLock action is executed after acquiring read lock so by locking write lock it is possible
	 *                  to synchronize start of executing actions
	 * @return CountDownLatch that allows to await for the action execution
	 */
	private CountDownLatch executeMultiThread(int threadsCount, int repeats, ReadWriteLock startLock,
			final Runnable action) {
		final CountDownLatch finished = new CountDownLatch(threadsCount * repeats);
		ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
		for(int i = 0; i < threadsCount; i++) {
			executorService.execute(() -> {
				for(int j = 0; j < repeats; j++) {
					try {
						startLock.readLock().lock(); // all threads should wait where for start
						action.run();
					} finally {
						startLock.readLock().unlock();
						finished.countDown();
					}
				}
			});
		}
		return finished;
	}

	@Test
	void shouldTransferMoneyWhenBothAccountExistsAndSufficientBalance() throws InterruptedException {
		final int initialBalance = 100_000;

		bank.createAccount(ACCOUNT_ID_1);
		bank.createAccount(ACCOUNT_ID_2);
		bank.changeBalance(ACCOUNT_ID_1, initialBalance);
		bank.changeBalance(ACCOUNT_ID_2, initialBalance);
		ReadWriteLock startLock = new ReentrantReadWriteLock();
		startLock.writeLock().lock();

		// when
		CountDownLatch to2 = executeMultiThread(5, 10_000, startLock, () -> {
			bank.transfer(new Transfer(ACCOUNT_ID_2, ACCOUNT_ID_1, 1));
		});
		CountDownLatch to1 = executeMultiThread(5, 10_000, startLock, () -> {
			bank.transfer(new Transfer(ACCOUNT_ID_1, ACCOUNT_ID_2, 1));
		});

		// start threads in the same time
		startLock.writeLock().unlock();

		to1.await();
		to2.await();

		// then
		int balance1 = bank.getBalance(ACCOUNT_ID_1);
		int balance2 = bank.getBalance(ACCOUNT_ID_2);
		assertEquals(initialBalance, balance1, "balance of account 1 is incorrect");
		assertEquals(initialBalance, balance2, "balance of account 2 is incorrect");
	}
}
