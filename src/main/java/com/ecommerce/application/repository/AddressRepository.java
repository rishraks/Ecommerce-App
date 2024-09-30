package com.ecommerce.application.repository;

import com.ecommerce.application.model.Address;
import com.ecommerce.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
