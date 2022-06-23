package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.Payment;
import com.erestaurant.customer.repository.PaymentRepository;
import com.erestaurant.customer.service.PaymentService;
import com.erestaurant.customer.service.dto.PaymentDTO;
import com.erestaurant.customer.service.mapper.PaymentMapper;
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
 * Service Implementation for managing {@link Payment}.
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Mono<PaymentDTO> save(PaymentDTO paymentDTO) {
        log.debug("Request to save Payment : {}", paymentDTO);
        return paymentRepository.save(paymentMapper.toEntity(paymentDTO)).map(paymentMapper::toDto);
    }

    @Override
    public Mono<PaymentDTO> update(PaymentDTO paymentDTO) {
        log.debug("Request to save Payment : {}", paymentDTO);
        return paymentRepository.save(paymentMapper.toEntity(paymentDTO).setIsPersisted()).map(paymentMapper::toDto);
    }

    @Override
    public Mono<PaymentDTO> partialUpdate(PaymentDTO paymentDTO) {
        log.debug("Request to partially update Payment : {}", paymentDTO);

        return paymentRepository
            .findById(paymentDTO.getId())
            .map(existingPayment -> {
                paymentMapper.partialUpdate(existingPayment, paymentDTO);

                return existingPayment;
            })
            .flatMap(paymentRepository::save)
            .map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PaymentDTO> findAll() {
        log.debug("Request to get all Payments");
        return paymentRepository.findAll().map(paymentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return paymentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PaymentDTO> findOne(String id) {
        log.debug("Request to get Payment : {}", id);
        return paymentRepository.findById(id).map(paymentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Payment : {}", id);
        return paymentRepository.deleteById(id);
    }
}
