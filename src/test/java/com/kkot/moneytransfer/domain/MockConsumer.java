package com.kkot.moneytransfer.domain;

import java.util.List;
import java.util.function.Consumer;

class MockConsumer implements Consumer<List<Account>> {
  private List<Account> accounts;

  @Override
  public void accept(final List<Account> accounts) {
    this.accounts = accounts;
  }

  public List<Account> getAccounts() {
    return accounts;
  }
}
