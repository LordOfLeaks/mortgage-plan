package com.olafparfienczyk.mortgageplan.repository;

import com.olafparfienczyk.mortgageplan.entity.Customer;
import com.olafparfienczyk.mortgageplan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findAllByUser_Id(UUID userId);

    void deleteByIdAndUser(UUID id, User user);

}