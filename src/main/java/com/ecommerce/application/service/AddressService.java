package com.ecommerce.application.service;


import com.ecommerce.application.model.User;
import com.ecommerce.application.payload.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO addAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getUserAddresses(User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    AddressDTO updateUserAddress(Long addressId, @Valid AddressDTO addressDTO, User user);

    String deleteUserAddress(Long addressId, User user);
}
