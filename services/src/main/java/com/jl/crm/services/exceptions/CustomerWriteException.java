package com.jl.crm.services.exceptions;

import com.jl.crm.services.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class CustomerWriteException extends CustomerException {

    private static final long serialVersionUID = 1L;

    public CustomerWriteException(Customer customer,Throwable cause) {
        super(customer, cause);
    }

}
