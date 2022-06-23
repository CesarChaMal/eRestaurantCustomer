package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.Cancel;
import com.erestaurant.customer.repository.CancelRepository;
import com.erestaurant.customer.service.CancelService;
import com.erestaurant.customer.service.dto.CancelDTO;
import com.erestaurant.customer.service.mapper.CancelMapper;
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
 * Service Implementation for managing {@link Cancel}.
 */
@Service
@Transactional
public class CancelServiceImpl implements CancelService {

    private final Logger log = LoggerFactory.getLogger(CancelServiceImpl.class);

    private final CancelRepository cancelRepository;

    private final CancelMapper cancelMapper;

    public CancelServiceImpl(CancelRepository cancelRepository, CancelMapper cancelMapper) {
        this.cancelRepository = cancelRepository;
        this.cancelMapper = cancelMapper;
    }

    @Override
    public Mono<CancelDTO> save(CancelDTO cancelDTO) {
        log.debug("Request to save Cancel : {}", cancelDTO);
        return cancelRepository.save(cancelMapper.toEntity(cancelDTO)).map(cancelMapper::toDto);
    }

    @Override
    public Mono<CancelDTO> update(CancelDTO cancelDTO) {
        log.debug("Request to save Cancel : {}", cancelDTO);
        return cancelRepository.save(cancelMapper.toEntity(cancelDTO).setIsPersisted()).map(cancelMapper::toDto);
    }

    @Override
    public Mono<CancelDTO> partialUpdate(CancelDTO cancelDTO) {
        log.debug("Request to partially update Cancel : {}", cancelDTO);

        return cancelRepository
            .findById(cancelDTO.getId())
            .map(existingCancel -> {
                cancelMapper.partialUpdate(existingCancel, cancelDTO);

                return existingCancel;
            })
            .flatMap(cancelRepository::save)
            .map(cancelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CancelDTO> findAll() {
        log.debug("Request to get all Cancels");
        return cancelRepository.findAll().map(cancelMapper::toDto);
    }

    public Mono<Long> countAll() {
        return cancelRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CancelDTO> findOne(String id) {
        log.debug("Request to get Cancel : {}", id);
        return cancelRepository.findById(id).map(cancelMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Cancel : {}", id);
        return cancelRepository.deleteById(id);
    }
}
