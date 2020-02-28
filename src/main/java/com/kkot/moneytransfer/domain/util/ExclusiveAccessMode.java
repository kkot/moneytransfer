package com.kkot.moneytransfer.domain.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class ExclusiveAccessMode implements AccessMode {
    public static final ExclusiveAccessMode INSTANCE = new ExclusiveAccessMode();

    @Override
    public Lock apply(ReadWriteLock readWriteLock) {
        return readWriteLock.writeLock();
    }

    @Override
    public String toString() {
        return "exclusive access mode";
    }
}
