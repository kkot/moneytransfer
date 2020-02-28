package com.kkot.moneytransfer.domain.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class SharedAccessMode implements AccessMode {
    public static final SharedAccessMode INSTANCE = new SharedAccessMode();

    @Override
    public Lock apply(ReadWriteLock readWriteLock) {
        return readWriteLock.readLock();
    }

    @Override
    public String toString() {
        return "shared access mode";
    }
}
