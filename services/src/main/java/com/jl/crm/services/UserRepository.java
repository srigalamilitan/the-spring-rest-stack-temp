package com.jl.crm.services;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Eric on 7/8/2015.
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUsername(@Param("username") String username);
    List<User> findUserByFirstNameOrLastNameOrUsername(@Param("firstName") String firstName,
                                                       @Param("lastName") String lastName,
                                                       @Param("username") String username);

}
