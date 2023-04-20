package com.olafparfienczyk.mortgageplan.service;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public static BusinessException unauthenticated() {
        return new BusinessException("Unauthenticated");
    }

    public static BusinessException userNotExists() {
        return new BusinessException("User not exists");
    }
}