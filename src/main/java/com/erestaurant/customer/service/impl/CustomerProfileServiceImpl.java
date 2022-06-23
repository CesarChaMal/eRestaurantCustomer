package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.CustomerProfile;
import com.erestaurant.customer.repository.CustomerProfileRepository;
import com.erestaurant.customer.service.CustomerProfileService;
import com.erestaurant.customer.service.dto.CustomerProfileDTO;
import com.erestaurant.customer.service.mapper.CustomerProfileMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link CustomerProfile}.
 */
@Service
@Transactional
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final Logger log = LoggerFactory.getLogger(CustomerProfileServiceImpl.class);

    private final CustomerProfileRepository customerProfileRepository;

    private final CustomerProfileMapper customerProfileMapper;

    public CustomerProfileServiceImpl(CustomerProfileRepository customerProfileRepository, CustomerProfileMapper customerProfileMapper) {
        this.customerProfileRepository = customerProfileRepository;
        this.customerProfileMapper = customerProfileMapper;
    }

    @Override
    public Mono<CustomerProfileDTO> save(CustomerProfileDTO customerProfileDTO) {
        log.debug("Request to save CustomerProfile : {}", customerProfileDTO);
        return customerProfileRepository.save(customerProfileMapper.toEntity(customerProfileDTO)).map(customerProfileMapper::toDto);
    }

    @Override
    public Mono<CustomerProfileDTO> update(CustomerProfileDTO customerProfileDTO) {
        log.debug("Request to save CustomerProfile : {}", customerProfileDTO);
        return customerProfileRepository
            .save(customerProfileMapper.toEntity(customerProfileDTO).setIsPersisted())
            .map(customerProfileMapper::toDto);
    }

    @Override
    public Mono<CustomerProfileDTO> partialUpdate(CustomerProfileDTO customerProfileDTO) {
        log.debug("Request to partially update CustomerProfile : {}", customerProfileDTO);

        return customerProfileRepository
            .findById(customerProfileDTO.getId())
            .map(existingCustomerProfile -> {
                customerProfileMapper.partialUpdate(existingCustomerProfile, customerProfileDTO);

                return existingCustomerProfile;
            })
            .flatMap(customerProfileRepository::save)
            .map(customerProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CustomerProfileDTO> findAll() {
        log.debug("Request to get all CustomerProfiles");
        return customerProfileRepository.findAll().map(customerProfileMapper::toDto);
    }

    public Mono<Long> countAll() {
        return customerProfileRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CustomerProfileDTO> findOne(String id) {
        log.debug("Request to get CustomerProfile : {}", id);
        return customerProfileRepository.findById(id).map(customerProfileMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete CustomerProfile : {}", id);
        return customerProfileRepository.deleteById(id);
    }
}
