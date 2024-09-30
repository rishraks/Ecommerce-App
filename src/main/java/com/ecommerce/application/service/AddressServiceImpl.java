package com.ecommerce.application.service;


import com.ecommerce.application.exceptions.APIException;
import com.ecommerce.application.exceptions.ResourceNotFoundException;
import com.ecommerce.application.model.Address;
import com.ecommerce.application.model.User;
import com.ecommerce.application.payload.AddressDTO;
import com.ecommerce.application.repository.AddressRepository;
import com.ecommerce.application.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public AddressDTO addAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> addressList = user.getAddresses();
        if (addressList.contains(address)) {
            throw new APIException("This address already exists");
        }
        addressList.add(address);
        user.setAddresses(addressList);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addressList = user.getAddresses();
        if (addressList.isEmpty()) {
            throw new APIException("No address found");
        }
        return addressList.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addressList = addressRepository.findAll();
        if (addressList.isEmpty()) {
            throw new APIException("No address found");
        }
        return addressList.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public AddressDTO updateUserAddress(Long addressId, AddressDTO addressDTO, User user) {
        Address incomingAddress = modelMapper.map(addressDTO, Address.class);
        Address existingAddress = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        if (!existingAddress.getUser().getUserId().equals(user.getUserId())) {
            throw new APIException("Address does not belong to the user");
        }
        if (existingAddress.equals(incomingAddress)) {
            throw new APIException("This address already exists");
        }
        existingAddress.setStreet(incomingAddress.getStreet());
        existingAddress.setBuildingName(incomingAddress.getBuildingName());
        existingAddress.setCity(incomingAddress.getCity());
        existingAddress.setCountry(incomingAddress.getCountry());
        existingAddress.setPincode(incomingAddress.getPincode());
        existingAddress.setPhoneNumber(existingAddress.getPhoneNumber());
        Address updatedAddress = addressRepository.save(existingAddress);

        User loggedInUser = existingAddress.getUser();
        loggedInUser.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        loggedInUser.getAddresses().add(updatedAddress);
        userRepository.save(loggedInUser);
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteUserAddress(Long addressId, User user) {
        Address existingAddress = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        if (!existingAddress.getUser().getUserId().equals(user.getUserId())) {
            throw new APIException("Address does not belong to the user");
        }
        User loggedInUser = existingAddress.getUser();
        loggedInUser.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(loggedInUser);

        addressRepository.delete(existingAddress);
        return "Deleted Address";
    }
}