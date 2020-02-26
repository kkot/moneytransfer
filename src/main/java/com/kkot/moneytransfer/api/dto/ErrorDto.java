package com.kkot.moneytransfer.api.dto;

import java.util.Objects;

public class ErrorDto {
    private String error;

    public ErrorDto(final String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ErrorDto))
            return false;
        final ErrorDto errorDto = (ErrorDto) o;
        return Objects.equals(error, errorDto.error);
    }

    @Override
    public int hashCode() {
        return error != null ? error.hashCode() : 0;
    }
}
