package com.kkot.moneytransfer.domain.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Function;

public interface AccessMode extends Function<ReadWriteLock, Lock> {
}
