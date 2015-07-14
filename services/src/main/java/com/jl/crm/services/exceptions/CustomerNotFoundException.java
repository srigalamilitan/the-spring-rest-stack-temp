package com.jl.crm.services.exceptions;

public class CustomerNotFoundException extends RuntimeException {

    private long customerId;

    public CustomerNotFoundException(long customerId) {
        super("Customer #" + customerId + " was not found");
        this.customerId = customerId;
    }

    public long getCustomerId() {
        return customerId;
    }

}
