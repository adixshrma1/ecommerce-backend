package com.aditya.project.service;

import com.aditya.project.exception.ResourceNotFoundException;
import com.aditya.project.model.Address;
import com.aditya.project.model.User;
import com.aditya.project.payload.AddressDTO;
import com.aditya.project.repository.AddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);

        user.getAddresses().add(address);
        address.getUsers().add(user);

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> allAddresses = addressRepository.findAll();
        return allAddresses.stream()
                .map(a -> modelMapper.map(a, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        return user.getAddresses().stream()
                .map(a -> modelMapper.map(a, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {
        Address address = modelMapper.map(addressDTO, Address.class);
        address.setAddressId(addressId);
        Address updated = addressRepository.save(address);
        return modelMapper.map(updated, AddressDTO.class);
    }

    @Override
    public AddressDTO deleteAddressById(Long addressId) {
        Address addressToDelete = addressRepository.findById(addressId)
                        .orElseThrow(()-> new ResourceNotFoundException("Address", "addressId", addressId));

        // manually removing addresses from users
        for(User user : addressToDelete.getUsers()){
            user.getAddresses().remove(addressToDelete);
        }

        addressRepository.delete(addressToDelete);
        return modelMapper.map(addressToDelete, AddressDTO.class);
    }


}
