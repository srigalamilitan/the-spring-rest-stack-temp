package com.jl.crm.services;

import com.jl.crm.services.exceptions.CustomerNotFoundException;
import com.jl.crm.services.exceptions.UserNotFoundException;
import com.jl.crm.services.exceptions.UserProfilePhotoReadException;
import com.jl.crm.services.exceptions.UserProfilePhotoWriteException;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;

@Service
@Transactional
public class JpaCrmService implements CrmService {

    private CustomerRepository customerRepository;
    private UserRepository userRepository;

    @Autowired
    public JpaCrmService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Collection<Customer> search(long userId, String token) {
        return this.customerRepository.search(userId, "%" + token + "%");
    }

    @Override
    public ProfilePhoto readUserProfilePhoto(long userId) {
        InputStream fileInputStream = null;
        try {
            User user = findById(userId);
            fileInputStream = new FileInputStream(fileForPhoto(userId));
            byte[] data = IOUtils.toByteArray(fileInputStream);
            return new ProfilePhoto(userId, data, MediaType.parseMediaType(user.getProfilePhotoMediaType()));
        } catch (Exception e) {
            throw new UserProfilePhotoReadException(userId, e);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    private File fileForPhoto(long userId) {
        return new File(ServiceConfiguration.CRM_STORAGE_PROFILES_DIRECTORY, Long.toString(userId));
    }

    @Override
    public void writeUserProfilePhoto(long userId, MediaType mediaType, byte[] bytesForProfilePhoto) {
        User user = findById(userId);
        user.setProfilePhotoMediaType(mediaType.toString());
        user.setProfilePhotoImported(true);
        userRepository.save(user);

        ByteArrayInputStream byteArrayInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileForPhoto(userId));
            byteArrayInputStream = new ByteArrayInputStream(bytesForProfilePhoto);
            IOUtils.copy(byteArrayInputStream, fileOutputStream);
        } catch (IOException e) {
            throw new UserProfilePhotoWriteException(userId, e);
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
            IOUtils.closeQuietly(byteArrayInputStream);
        }
    }

    @Override
    public User findById(long userId) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user;
    }

    @Override
    public User createUser(String username, String password, String firstName, String lastName) {
        User user = new User(username, password, firstName, lastName);
        this.userRepository.save(user);
        return user;
    }

    @Override
    public User removeUser(long userId) {
        User user = userRepository.findOne(userId);
        this.userRepository.delete(user);
        return user;
    }

    @Override
    public User updateUser(long userId, String username, String password, String firstName, String lastName) {
        User user = this.userRepository.findOne(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return this.userRepository.save(user);
    }

    @Override
    public User findbyUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public Customer removeCustomer(long userId, long customerId) {
        User user = this.userRepository.findOne(userId);
        Customer customer = this.customerRepository.findOne(customerId);
        user.getCustomers().remove(customer);
        this.userRepository.save(user);
        customer.setUser(null);
        this.customerRepository.delete(customer);
        return customer;
    }

    @Override
    public Customer addCustomer(long userId, String firstName, String lastName) {
        User user = this.userRepository.findOne(userId);
        Date signupDate = new Date();
        Customer customer = new Customer(user, firstName, lastName, signupDate);
        this.customerRepository.save(customer);
        return customer;
    }

    @Override
    public Collection<Customer> loadCustomerAccounts(long userId) {
        List<Customer> customersList = this.customerRepository.findByUserId(userId);
        ArrayList<Customer> customers = new ArrayList<>();
        for (Customer c : customersList) {
            Hibernate.initialize(c);
            User user = new User(userId);
            c.setUser(user);
            customers.add(c);
        }
        return Collections.unmodifiableList(customers);
    }

    @Override
    public Customer findCustomerById(long customerId) {
        Customer customer = this.customerRepository.findOne(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException(customerId);
        }
        Hibernate.initialize(customer.getUser());
        return customer;
    }
}
