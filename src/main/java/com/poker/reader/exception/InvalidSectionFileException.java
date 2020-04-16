package com.poker.reader.exception;

public class InvalidSectionFileException extends RuntimeException {
    public InvalidSectionFileException(String section) {
        super("Invalid file section found: " + section);
    }
}
