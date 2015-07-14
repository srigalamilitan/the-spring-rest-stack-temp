package com.jl.crm.services;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Eric on 7/8/2015.
 */
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

    List<Customer> findByUserId(@Param("userId") Long userId);

    @Query("select c from Customer c where c.user.id = :userId and (LOWER(CONCAT(c.firstName, c.lastName)) LIKE :q)")
    List<Customer> search(@Param("userId") Long userId,
                          @Param("q") String query);

}
