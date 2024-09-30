package com.ecommerce.application.controller;


import com.ecommerce.application.model.User;
import com.ecommerce.application.payload.AddressDTO;
import com.ecommerce.application.service.AddressService;
import com.ecommerce.application.util.AuthenticationUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthenticationUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        AddressDTO savedAddress = addressService.addAddress(addressDTO, user);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }


    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> allAddresses = addressService.getAllAddresses();
        return new ResponseEntity<>(allAddresses, HttpStatus.FOUND);
    }


    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses() {
        User user = authUtil.loggedInUser();
        List<AddressDTO> existingAddresses = addressService.getUserAddresses(user);
        return new ResponseEntity<>(existingAddresses, HttpStatus.FOUND);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        AddressDTO addressById = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressById, HttpStatus.FOUND);
    }


    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateUserAddress(@PathVariable Long addressId, @Valid @RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        AddressDTO updateAddress = addressService.updateUserAddress(addressId, addressDTO, user);
        return new ResponseEntity<>(updateAddress, HttpStatus.OK);
    }


    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteUserAddress(@PathVariable Long addressId) {
        User user = authUtil.loggedInUser();
        String deleteAddress = addressService.deleteUserAddress(addressId, user);
        return new ResponseEntity<>(deleteAddress, HttpStatus.OK);
    }

}
