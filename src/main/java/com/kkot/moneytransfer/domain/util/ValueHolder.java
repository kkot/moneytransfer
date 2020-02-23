package com.kkot.moneytransfer.domain.util;

public class ValueHolder<T> {
	private T value;

	public ValueHolder(final T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(final T value) {
		this.value = value;
	}
}
