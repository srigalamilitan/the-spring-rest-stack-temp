package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.Customer;
import com.jl.crm.services.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@ExposesResourceFor(User.class)
@RequestMapping(value = "/users")
public class UserController {

    private final CrmService crmService;
    private final ResourceAssembler<User, Resource<User>> userResourceAssembler;
    private final ResourceAssembler<Customer, Resource<Customer>> customerResourceAssembler;

    @Autowired
    public UserController(CrmService crmService, ResourceAssembler<User, Resource<User>> userResourceAssembler, ResourceAssembler<Customer, Resource<Customer>> customerResourceAssembler) {
        this.crmService = crmService;
        this.userResourceAssembler = userResourceAssembler;
        this.customerResourceAssembler = customerResourceAssembler;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user}")
    public ResponseEntity<Resource<User>> loadUser(@PathVariable Long userId) {
        return Optional.of(this.crmService.findById(userId))
            .map(u -> new ResponseEntity<>(userResourceAssembler.toResource(u), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = RequestMethod.GET, value = "{user}/customers")
    public Resources<Resource<Customer>> loadUserCustomers(@PathVariable Long userId) {
        List<Resource<Customer>> customers = this.crmService.loadCustomerAccounts(userId).parallelStream()
            .map(customerResourceAssembler::toResource)
            .collect(Collectors.toList());
        Resources<Resource<Customer>> customerResources = new Resources<>(customers);
        return customerResources;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user}/customers/{customer}")
    public ResponseEntity<Resource<Customer>> loadSingleUserCustomer(@PathVariable Long userId,
                                                                     @PathVariable Long customerId) {
        return Optional.of(this.crmService.findCustomerById(customerId))
            .map(u -> new ResponseEntity<>(customerResourceAssembler.toResource(u), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{user}/customers")
    public ResponseEntity<Void> addCustomer(@PathVariable Long userId, @PathVariable Customer c) {
        Customer customer = crmService.addCustomer(userId, c.getFirstName(), c.getLastName());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(linkTo(methodOn(getClass()).loadSingleUserCustomer(userId, customer.getId())).toUri());

        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{user}")
    public ResponseEntity<Resource<User>> deleteUser(@PathVariable Long userId) {
        return new ResponseEntity<>(userResourceAssembler.toResource(this.crmService.removeUser(userId)), HttpStatus.NOT_FOUND);
    }



}