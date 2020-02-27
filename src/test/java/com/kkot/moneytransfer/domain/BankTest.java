package com.kkot.moneytransfer.domain;

import com.kkot.moneytransfer.domain.status.AccountIsMissingStatus;
import com.kkot.moneytransfer.domain.status.InsufficientBalanceStatus;
import com.kkot.moneytransfer.domain.status.OkStatus;
import com.kkot.moneytransfer.domain.status.OperationStatus;
import com.kkot.moneytransfer.domain.valueobject.AccountId;
import com.kkot.moneytransfer.domain.valueobject.Transfer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class BankTest {
    private static final AccountId ACCOUNT_ID_1 = AccountId.of("1");
    private static final AccountId ACCOUNT_ID_2 = AccountId.of("2");

    private Bank bank;

    @BeforeEach
    void setUp() {
        bank = new Bank(new AccountsStore());
    }

    private void initialiseAccount(final AccountId accountId, final int initialBalance) {
        bank.createAccount(accountId);
        bank.changeBalance(accountId, initialBalance);
    }

    @Test
    void createdAccountShouldHaveBalance0() {
        // given
        bank.createAccount(ACCOUNT_ID_1);

        // when
        int balance = bank.getBalance(ACCOUNT_ID_1);

        // then
        assertEquals(0, balance);
    }

    @Test
    void shouldReturn0ForNotExistingAccount() {
        // when
        int balance = bank.getBalance(ACCOUNT_ID_1);

        // then
        assertEquals(0, balance);
    }

    @Test
    void changeBalanceShouldReturnAccountIsMissingStatusIfAccountDoesntExist() {
        // when
        OperationStatus operationStatus = bank.changeBalance(ACCOUNT_ID_1, 20);

        // then
        assertTrue(operationStatus instanceof AccountIsMissingStatus);
        assertEquals(ACCOUNT_ID_1, ((AccountIsMissingStatus) operationStatus).getAccountId());
    }

    @Test
    void shouldChangeBalanceWhenAccountExistsAndHasZeroBalance() {
        // given
        bank.createAccount(ACCOUNT_ID_1);

        // when
        OperationStatus operationStatus = bank.changeBalance(ACCOUNT_ID_1, 20);

        // then
        assertTrue(operationStatus instanceof OkStatus);
        assertEquals(20, bank.getBalance(ACCOUNT_ID_1));
    }

    @Test
    void shouldChangeBalanceWhenAccountExistsAndHasNonZeroBalance() {
        // given
        bank.createAccount(ACCOUNT_ID_1);
        bank.changeBalance(ACCOUNT_ID_1, 20);

        // when
        OperationStatus operationStatus = bank.changeBalance(ACCOUNT_ID_1, 20);

        // then
        assertTrue(operationStatus instanceof OkStatus);
        assertEquals(40, bank.getBalance(ACCOUNT_ID_1));
    }

    @Test
    void shouldNotChangeBalanceWhenTryingToCreateAccountAgain() {
        // given
        initialiseAccount(ACCOUNT_ID_1, 20);

        // when
        bank.createAccount(ACCOUNT_ID_1);

        // then
        assertEquals(20, bank.getBalance(ACCOUNT_ID_1));
    }

    @Test
    void shouldTransferMoneyWhenBothAccountExistsAndSufficientBalance() {
        // given
        initialiseAccount(ACCOUNT_ID_1, 200);
        initialiseAccount(ACCOUNT_ID_2, 0);

        // when
        OperationStatus operationStatus = bank.transfer(new Transfer(ACCOUNT_ID_1, ACCOUNT_ID_2, 50));

        // then
        assertEquals(operationStatus.getClass(), OkStatus.class);
        assertEquals(150, bank.getBalance(ACCOUNT_ID_1));
        assertEquals(50, bank.getBalance(ACCOUNT_ID_2));
    }

    @Test
    void shouldTransferMoneyWhenBothAccountExistsAndSourceBalanceEqualsAmount() {
        // given
        initialiseAccount(ACCOUNT_ID_1, 200);
        initialiseAccount(ACCOUNT_ID_2, 0);

        // when
        OperationStatus operationStatus = bank.transfer(new Transfer(ACCOUNT_ID_1, ACCOUNT_ID_2, 200));

        // then
        assertEquals(operationStatus.getClass(), OkStatus.class);
        assertEquals(0, bank.getBalance(ACCOUNT_ID_1));
        assertEquals(200, bank.getBalance(ACCOUNT_ID_2));
    }

    @Test
    void shouldNotTransferMoneyWhenSourceAccountDoesNotExist() {
        // given
        initialiseAccount(ACCOUNT_ID_2, 200);

        // when
        OperationStatus operationStatus = bank.transfer(new Transfer(ACCOUNT_ID_1, ACCOUNT_ID_2, 50));

        // then
        assertEquals(operationStatus.getClass(), AccountIsMissingStatus.class);
        assertEquals(ACCOUNT_ID_1, ((AccountIsMissingStatus) operationStatus).getAccountId());
    }

    @Test
    void shouldNotTransferMoneyWhenTargetAccountDoesNotExist() {
        // given
        initialiseAccount(ACCOUNT_ID_1, 200);

        // when
        OperationStatus operationStatus = bank.transfer(new Transfer(ACCOUNT_ID_1, ACCOUNT_ID_2, 50));

        // then
        assertEquals(operationStatus.getClass(), AccountIsMissingStatus.class);
        assertEquals(ACCOUNT_ID_2, ((AccountIsMissingStatus) operationStatus).getAccountId());
    }

    @Test
    void shouldNotTransferMoneyWhenSourceAccountHasNotSufficientBalance() {
        // given
        initialiseAccount(ACCOUNT_ID_1, 50);
        initialiseAccount(ACCOUNT_ID_2, 0);

        // when
        OperationStatus operationStatus = bank.transfer(new Transfer(ACCOUNT_ID_1, ACCOUNT_ID_2, 51));

        // then
        assertEquals(operationStatus.getClass(), InsufficientBalanceStatus.class);
        assertEquals(50, bank.getBalance(ACCOUNT_ID_1));
        assertEquals(0, bank.getBalance(ACCOUNT_ID_2));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -50})
    void shouldNotTransferMoneyWhenAmountIs0OrNegative(int amount) {
        // given
        initialiseAccount(ACCOUNT_ID_1, 50);
        initialiseAccount(ACCOUNT_ID_2, 0);

        // when
        OperationStatus operationStatus = bank.transfer(new Transfer(ACCOUNT_ID_1, ACCOUNT_ID_2, 51));

        // then
        assertEquals(operationStatus.getClass(), InsufficientBalanceStatus.class);
        assertEquals(50, bank.getBalance(ACCOUNT_ID_1));
        assertEquals(0, bank.getBalance(ACCOUNT_ID_2));
    }
}
