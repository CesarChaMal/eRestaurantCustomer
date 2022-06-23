package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.Order;
import com.erestaurant.customer.repository.OrderRepository;
import com.erestaurant.customer.service.OrderService;
import com.erestaurant.customer.service.dto.OrderDTO;
import com.erestaurant.customer.service.mapper.OrderMapper;
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
 * Service Implementation for managing {@link Order}.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Mono<OrderDTO> save(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        return orderRepository.save(orderMapper.toEntity(orderDTO)).map(orderMapper::toDto);
    }

    @Override
    public Mono<OrderDTO> update(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        return orderRepository.save(orderMapper.toEntity(orderDTO).setIsPersisted()).map(orderMapper::toDto);
    }

    @Override
    public Mono<OrderDTO> partialUpdate(OrderDTO orderDTO) {
        log.debug("Request to partially update Order : {}", orderDTO);

        return orderRepository
            .findById(orderDTO.getId())
            .map(existingOrder -> {
                orderMapper.partialUpdate(existingOrder, orderDTO);

                return existingOrder;
            })
            .flatMap(orderRepository::save)
            .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderDTO> findAll() {
        log.debug("Request to get all Orders");
        return orderRepository.findAll().map(orderMapper::toDto);
    }

    public Mono<Long> countAll() {
        return orderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrderDTO> findOne(String id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findById(id).map(orderMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Order : {}", id);
        return orderRepository.deleteById(id);
    }
}
