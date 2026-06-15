package com.example.legacy.customer;

public interface CustomerRepository {

    void save(Customer customer);

    Customer findById(Long id);
}