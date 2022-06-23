package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.OnHold;
import com.erestaurant.customer.repository.OnHoldRepository;
import com.erestaurant.customer.service.OnHoldService;
import com.erestaurant.customer.service.dto.OnHoldDTO;
import com.erestaurant.customer.service.mapper.OnHoldMapper;
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
 * Service Implementation for managing {@link OnHold}.
 */
@Service
@Transactional
public class OnHoldServiceImpl implements OnHoldService {

    private final Logger log = LoggerFactory.getLogger(OnHoldServiceImpl.class);

    private final OnHoldRepository onHoldRepository;

    private final OnHoldMapper onHoldMapper;

    public OnHoldServiceImpl(OnHoldRepository onHoldRepository, OnHoldMapper onHoldMapper) {
        this.onHoldRepository = onHoldRepository;
        this.onHoldMapper = onHoldMapper;
    }

    @Override
    public Mono<OnHoldDTO> save(OnHoldDTO onHoldDTO) {
        log.debug("Request to save OnHold : {}", onHoldDTO);
        return onHoldRepository.save(onHoldMapper.toEntity(onHoldDTO)).map(onHoldMapper::toDto);
    }

    @Override
    public Mono<OnHoldDTO> update(OnHoldDTO onHoldDTO) {
        log.debug("Request to save OnHold : {}", onHoldDTO);
        return onHoldRepository.save(onHoldMapper.toEntity(onHoldDTO).setIsPersisted()).map(onHoldMapper::toDto);
    }

    @Override
    public Mono<OnHoldDTO> partialUpdate(OnHoldDTO onHoldDTO) {
        log.debug("Request to partially update OnHold : {}", onHoldDTO);

        return onHoldRepository
            .findById(onHoldDTO.getId())
            .map(existingOnHold -> {
                onHoldMapper.partialUpdate(existingOnHold, onHoldDTO);

                return existingOnHold;
            })
            .flatMap(onHoldRepository::save)
            .map(onHoldMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OnHoldDTO> findAll() {
        log.debug("Request to get all OnHolds");
        return onHoldRepository.findAll().map(onHoldMapper::toDto);
    }

    public Mono<Long> countAll() {
        return onHoldRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OnHoldDTO> findOne(String id) {
        log.debug("Request to get OnHold : {}", id);
        return onHoldRepository.findById(id).map(onHoldMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete OnHold : {}", id);
        return onHoldRepository.deleteById(id);
    }
}
