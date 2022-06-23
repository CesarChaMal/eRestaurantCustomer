package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.NewOrder;
import com.erestaurant.customer.repository.NewOrderRepository;
import com.erestaurant.customer.service.NewOrderService;
import com.erestaurant.customer.service.dto.NewOrderDTO;
import com.erestaurant.customer.service.mapper.NewOrderMapper;
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
 * Service Implementation for managing {@link NewOrder}.
 */
@Service
@Transactional
public class NewOrderServiceImpl implements NewOrderService {

    private final Logger log = LoggerFactory.getLogger(NewOrderServiceImpl.class);

    private final NewOrderRepository newOrderRepository;

    private final NewOrderMapper newOrderMapper;

    public NewOrderServiceImpl(NewOrderRepository newOrderRepository, NewOrderMapper newOrderMapper) {
        this.newOrderRepository = newOrderRepository;
        this.newOrderMapper = newOrderMapper;
    }

    @Override
    public Mono<NewOrderDTO> save(NewOrderDTO newOrderDTO) {
        log.debug("Request to save NewOrder : {}", newOrderDTO);
        return newOrderRepository.save(newOrderMapper.toEntity(newOrderDTO)).map(newOrderMapper::toDto);
    }

    @Override
    public Mono<NewOrderDTO> update(NewOrderDTO newOrderDTO) {
        log.debug("Request to save NewOrder : {}", newOrderDTO);
        return newOrderRepository.save(newOrderMapper.toEntity(newOrderDTO).setIsPersisted()).map(newOrderMapper::toDto);
    }

    @Override
    public Mono<NewOrderDTO> partialUpdate(NewOrderDTO newOrderDTO) {
        log.debug("Request to partially update NewOrder : {}", newOrderDTO);

        return newOrderRepository
            .findById(newOrderDTO.getId())
            .map(existingNewOrder -> {
                newOrderMapper.partialUpdate(existingNewOrder, newOrderDTO);

                return existingNewOrder;
            })
            .flatMap(newOrderRepository::save)
            .map(newOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<NewOrderDTO> findAll() {
        log.debug("Request to get all NewOrders");
        return newOrderRepository.findAll().map(newOrderMapper::toDto);
    }

    public Mono<Long> countAll() {
        return newOrderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<NewOrderDTO> findOne(String id) {
        log.debug("Request to get NewOrder : {}", id);
        return newOrderRepository.findById(id).map(newOrderMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete NewOrder : {}", id);
        return newOrderRepository.deleteById(id);
    }
}
