package com.jl.crm.client;

import java.util.Date;

/**
 * Created by eric on 7/6/15.
 */
public class Customer {

    private String firstName, lastName;
    private Date signupDate;
    private User user;
    private Long id;

    public Customer(String firstName, String lastName, Date signupDate, User user, Long id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.signupDate = signupDate;
        this.user = user;
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(Date signupDate) {
        this.signupDate = signupDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
