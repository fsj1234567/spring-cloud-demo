package com.gavin.business.service.impl;

import com.gavin.business.domain.Address;
import com.gavin.business.domain.District;
import com.gavin.common.dto.address.AddressDto;
import com.gavin.common.dto.address.RegisterAddressDto;
import com.gavin.common.exception.RecordNotFoundException;
import com.gavin.business.repository.AddressRepository;
import com.gavin.business.repository.DistrictRepository;
import com.gavin.business.service.AddressService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public AddressDto registerAddress(RegisterAddressDto _address) {
        String districtId = _address.getDistrictId();
        District district = Optional.ofNullable(districtRepository.findOne(districtId))
                .orElseThrow(() -> new RecordNotFoundException("district", districtId));

        Address address = modelMapper.map(_address, Address.class);
        address.setDistrict(district);

        addressRepository.save(address);

        AddressDto addressDto = modelMapper.map(address, AddressDto.class);
        log.debug("register address successfully. {}", new Gson().toJson(addressDto));

        return addressDto;
    }

    @Override
    public AddressDto findAddressById(String _addressId) {
        Address address = Optional.ofNullable(addressRepository.findOne(_addressId))
                .orElseThrow(() -> new RecordNotFoundException("address", _addressId));

        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> findAddressesByUserId(String _userId) {
        List<Address> addresses = addressRepository.findByUserId(_userId);

        List<AddressDto> addressDtos = new ArrayList<>();
        addresses.forEach(
                address -> {
                    AddressDto addressDto = modelMapper.map(address, AddressDto.class);
                    addressDtos.add(addressDto);
                }
        );

        return addressDtos;
    }

    @Override
    public AddressDto findDefaultAddressByUserId(String _userId) {
        Address address = Optional.ofNullable(addressRepository.findByUserIdAndDefaultFlag(_userId, true))
                .orElseThrow(() -> new RecordNotFoundException("address", String.format("userId=%s", _userId)));

        return modelMapper.map(address, AddressDto.class);
    }

}