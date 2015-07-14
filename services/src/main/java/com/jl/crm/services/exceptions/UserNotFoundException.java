package com.jl.crm.services.exceptions;

public class UserNotFoundException extends RuntimeException {

    private long userId;

    public UserNotFoundException(long userId) {
        super("User #" + userId + " was not found");
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}
