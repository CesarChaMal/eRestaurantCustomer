package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.Complete;
import com.erestaurant.customer.repository.CompleteRepository;
import com.erestaurant.customer.service.CompleteService;
import com.erestaurant.customer.service.dto.CompleteDTO;
import com.erestaurant.customer.service.mapper.CompleteMapper;
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
 * Service Implementation for managing {@link Complete}.
 */
@Service
@Transactional
public class CompleteServiceImpl implements CompleteService {

    private final Logger log = LoggerFactory.getLogger(CompleteServiceImpl.class);

    private final CompleteRepository completeRepository;

    private final CompleteMapper completeMapper;

    public CompleteServiceImpl(CompleteRepository completeRepository, CompleteMapper completeMapper) {
        this.completeRepository = completeRepository;
        this.completeMapper = completeMapper;
    }

    @Override
    public Mono<CompleteDTO> save(CompleteDTO completeDTO) {
        log.debug("Request to save Complete : {}", completeDTO);
        return completeRepository.save(completeMapper.toEntity(completeDTO)).map(completeMapper::toDto);
    }

    @Override
    public Mono<CompleteDTO> update(CompleteDTO completeDTO) {
        log.debug("Request to save Complete : {}", completeDTO);
        return completeRepository.save(completeMapper.toEntity(completeDTO).setIsPersisted()).map(completeMapper::toDto);
    }

    @Override
    public Mono<CompleteDTO> partialUpdate(CompleteDTO completeDTO) {
        log.debug("Request to partially update Complete : {}", completeDTO);

        return completeRepository
            .findById(completeDTO.getId())
            .map(existingComplete -> {
                completeMapper.partialUpdate(existingComplete, completeDTO);

                return existingComplete;
            })
            .flatMap(completeRepository::save)
            .map(completeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CompleteDTO> findAll() {
        log.debug("Request to get all Completes");
        return completeRepository.findAll().map(completeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return completeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CompleteDTO> findOne(String id) {
        log.debug("Request to get Complete : {}", id);
        return completeRepository.findById(id).map(completeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Complete : {}", id);
        return completeRepository.deleteById(id);
    }
}
