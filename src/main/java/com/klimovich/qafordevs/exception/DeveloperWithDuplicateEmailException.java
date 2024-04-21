package com.klimovich.qafordevs.exception;

public class DeveloperWithDuplicateEmailException extends RuntimeException {
    public DeveloperWithDuplicateEmailException(String message) {
        super(message);
    }
}
