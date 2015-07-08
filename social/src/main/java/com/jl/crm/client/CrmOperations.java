package com.jl.crm.client;

import org.springframework.http.MediaType;

import java.util.Collection;
import java.util.Date;

/**
 * Created by eric on 7/6/15.
 */
public interface CrmOperations {

    User currentUser();
    Customer loadUserCustomer(Long id);
    User user(Long id);
    Customer createCustomer(String firstName, String lastName, Date signupDate);
    Collection<Customer> loadAllUserCustomers();
    void removeCustomer(Long id);
    void setProfilePhoto(byte[] bytesOfImage, MediaType mediaType);
    Customer updateCustomer(Long id, String firstName, String lastName);
    ProfilePhoto getUserProfilePhoto();
    Collection<Customer> search(String token);

}
