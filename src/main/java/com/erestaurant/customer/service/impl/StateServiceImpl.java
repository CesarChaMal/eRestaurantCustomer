package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.State;
import com.erestaurant.customer.repository.StateRepository;
import com.erestaurant.customer.service.StateService;
import com.erestaurant.customer.service.dto.StateDTO;
import com.erestaurant.customer.service.mapper.StateMapper;
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
 * Service Implementation for managing {@link State}.
 */
@Service
@Transactional
public class StateServiceImpl implements StateService {

    private final Logger log = LoggerFactory.getLogger(StateServiceImpl.class);

    private final StateRepository stateRepository;

    private final StateMapper stateMapper;

    public StateServiceImpl(StateRepository stateRepository, StateMapper stateMapper) {
        this.stateRepository = stateRepository;
        this.stateMapper = stateMapper;
    }

    @Override
    public Mono<StateDTO> save(StateDTO stateDTO) {
        log.debug("Request to save State : {}", stateDTO);
        return stateRepository.save(stateMapper.toEntity(stateDTO)).map(stateMapper::toDto);
    }

    @Override
    public Mono<StateDTO> update(StateDTO stateDTO) {
        log.debug("Request to save State : {}", stateDTO);
        return stateRepository.save(stateMapper.toEntity(stateDTO).setIsPersisted()).map(stateMapper::toDto);
    }

    @Override
    public Mono<StateDTO> partialUpdate(StateDTO stateDTO) {
        log.debug("Request to partially update State : {}", stateDTO);

        return stateRepository
            .findById(stateDTO.getId())
            .map(existingState -> {
                stateMapper.partialUpdate(existingState, stateDTO);

                return existingState;
            })
            .flatMap(stateRepository::save)
            .map(stateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StateDTO> findAll() {
        log.debug("Request to get all States");
        return stateRepository.findAll().map(stateMapper::toDto);
    }

    public Mono<Long> countAll() {
        return stateRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<StateDTO> findOne(String id) {
        log.debug("Request to get State : {}", id);
        return stateRepository.findById(id).map(stateMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete State : {}", id);
        return stateRepository.deleteById(id);
    }
}
