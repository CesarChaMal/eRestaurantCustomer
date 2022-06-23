package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.Refunded;
import com.erestaurant.customer.repository.RefundedRepository;
import com.erestaurant.customer.service.RefundedService;
import com.erestaurant.customer.service.dto.RefundedDTO;
import com.erestaurant.customer.service.mapper.RefundedMapper;
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
 * Service Implementation for managing {@link Refunded}.
 */
@Service
@Transactional
public class RefundedServiceImpl implements RefundedService {

    private final Logger log = LoggerFactory.getLogger(RefundedServiceImpl.class);

    private final RefundedRepository refundedRepository;

    private final RefundedMapper refundedMapper;

    public RefundedServiceImpl(RefundedRepository refundedRepository, RefundedMapper refundedMapper) {
        this.refundedRepository = refundedRepository;
        this.refundedMapper = refundedMapper;
    }

    @Override
    public Mono<RefundedDTO> save(RefundedDTO refundedDTO) {
        log.debug("Request to save Refunded : {}", refundedDTO);
        return refundedRepository.save(refundedMapper.toEntity(refundedDTO)).map(refundedMapper::toDto);
    }

    @Override
    public Mono<RefundedDTO> update(RefundedDTO refundedDTO) {
        log.debug("Request to save Refunded : {}", refundedDTO);
        return refundedRepository.save(refundedMapper.toEntity(refundedDTO).setIsPersisted()).map(refundedMapper::toDto);
    }

    @Override
    public Mono<RefundedDTO> partialUpdate(RefundedDTO refundedDTO) {
        log.debug("Request to partially update Refunded : {}", refundedDTO);

        return refundedRepository
            .findById(refundedDTO.getId())
            .map(existingRefunded -> {
                refundedMapper.partialUpdate(existingRefunded, refundedDTO);

                return existingRefunded;
            })
            .flatMap(refundedRepository::save)
            .map(refundedMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RefundedDTO> findAll() {
        log.debug("Request to get all Refundeds");
        return refundedRepository.findAll().map(refundedMapper::toDto);
    }

    public Mono<Long> countAll() {
        return refundedRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RefundedDTO> findOne(String id) {
        log.debug("Request to get Refunded : {}", id);
        return refundedRepository.findById(id).map(refundedMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Refunded : {}", id);
        return refundedRepository.deleteById(id);
    }
}
