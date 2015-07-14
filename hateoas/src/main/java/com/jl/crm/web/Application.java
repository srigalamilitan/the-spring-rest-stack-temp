package com.jl.crm.web;

import com.jl.crm.services.Customer;
import com.jl.crm.services.User;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import javax.servlet.MultipartConfigElement;
import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

    @Bean
    ResourceAssembler<User, Resource<User>> userResourceAssembler() {
        return (u) -> {
            try {
                String customerRel = "customers";
                String photoRel = "photo";
                User user = new User(u);
                user.setPassword(null);
                long userId = user.getId();
                Collection<Link> links = new ArrayList<>();
                links.add(linkTo(methodOn(UserController.class).loadUser(userId)).withSelfRel());
                links.add(linkTo(methodOn(UserController.class).loadUserCustomers(userId)).withSelfRel());
                links.add(linkTo(methodOn(UserProfilePhotoController.class).loadUserProfilePhoto(userId)).withSelfRel());
                return new Resource<>(user, links);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean
    ResourceAssembler<Customer, Resource<Customer>> customerResourceAssembler() {
        return (customer) -> {
            String userRel = "user";
            Class<UserController> controllerClass = UserController.class;
            Long userId = customer.getUser().getId();
            customer.setUser(null);
            Resource<Customer> customerResource = new Resource<>(customer);
            customerResource.add(linkTo(methodOn(controllerClass).loadSingleUserCustomer(userId, customer.getId())).withSelfRel());
            customerResource.add(linkTo(methodOn(controllerClass).loadUser(userId)).withSelfRel());
            return customerResource;
        };
    }

}
