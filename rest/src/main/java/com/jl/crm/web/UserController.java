package com.jl.crm.web;

import com.jl.crm.services.CRMService;
import com.jl.crm.services.Customer;
import com.jl.crm.services.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private CRMService crmService;

    @Autowired
    public UserController(CRMService crmService) {
        this.crmService = crmService;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{user}")
    ResponseEntity<User> deleteUser(@PathVariable long user) {
        return new ResponseEntity<>(crmService.removeUser(user), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user}")
    ResponseEntity<User> loadUser(@PathVariable Long user) {
        return Optional.of(this.crmService.findById(user))
            .map(u -> new ResponseEntity<>(u, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user}/customers")
    Collection<Customer> loadUserCustomers(@PathVariable Long user) {
        return this.crmService.loadCustomerAccounts(user);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user}/customers/{customer}")
    Customer loadSingleUserCustomer(@PathVariable Long user, @PathVariable Long customer) {
        return crmService.findCustomerById(customer);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{user}/customers")
    ResponseEntity<Customer> addCustomer(@PathVariable Long user, @RequestBody Customer c) {
        Customer customer = crmService.addCustomer(user, c.getFirstName(), c.getLastName());
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/users/{user}/customers/{customer}")
            .buildAndExpand(user, customer.getId())
            .toUri();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uriOfNewResource);

        return new ResponseEntity<>(customer, httpHeaders, HttpStatus.CREATED);
    }

}
