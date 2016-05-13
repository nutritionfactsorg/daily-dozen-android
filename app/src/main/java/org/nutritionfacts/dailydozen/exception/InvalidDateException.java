package org.nutritionfacts.dailydozen.exception;

public class InvalidDateException extends Exception {
    private final String invalidDateString;

    public InvalidDateException(String invalidDateString) {
        this.invalidDateString = invalidDateString;
    }

    @Override
    public String toString() {
        return String.format("InvalidDateException{invalidDateString='%s'}", invalidDateString);
    }
}
