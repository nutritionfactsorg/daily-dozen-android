package org.nutritionfacts.dailydozen.exception;

import androidx.annotation.NonNull;

public class InvalidDateException extends Exception {
    private final String invalidDateString;

    public InvalidDateException(String invalidDateString) {
        this.invalidDateString = invalidDateString;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("InvalidDateException{invalidDateString='%s'}", invalidDateString);
    }
}
