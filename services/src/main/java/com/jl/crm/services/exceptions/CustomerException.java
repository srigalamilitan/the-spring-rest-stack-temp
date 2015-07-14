package com.jl.crm.services.exceptions;

import com.jl.crm.services.Customer;

/**
 * Created by Eric on 7/8/2015.
 */
public class CustomerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustomerException(Customer customer, Throwable cause) {
        this(customer.getId(), cause);
    }

    public CustomerException(long customerId, Throwable cause) {
        super("Could not update customer #" + customerId, cause);
    }

    public CustomerException(Customer customer) {
        this(customer.getId());
    }

    public CustomerException(long customerId) {
        super("Could not update customer #" + customerId);
    }

}
