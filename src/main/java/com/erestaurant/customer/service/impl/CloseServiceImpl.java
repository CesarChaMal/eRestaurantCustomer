package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.Close;
import com.erestaurant.customer.repository.CloseRepository;
import com.erestaurant.customer.service.CloseService;
import com.erestaurant.customer.service.dto.CloseDTO;
import com.erestaurant.customer.service.mapper.CloseMapper;
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
 * Service Implementation for managing {@link Close}.
 */
@Service
@Transactional
public class CloseServiceImpl implements CloseService {

    private final Logger log = LoggerFactory.getLogger(CloseServiceImpl.class);

    private final CloseRepository closeRepository;

    private final CloseMapper closeMapper;

    public CloseServiceImpl(CloseRepository closeRepository, CloseMapper closeMapper) {
        this.closeRepository = closeRepository;
        this.closeMapper = closeMapper;
    }

    @Override
    public Mono<CloseDTO> save(CloseDTO closeDTO) {
        log.debug("Request to save Close : {}", closeDTO);
        return closeRepository.save(closeMapper.toEntity(closeDTO)).map(closeMapper::toDto);
    }

    @Override
    public Mono<CloseDTO> update(CloseDTO closeDTO) {
        log.debug("Request to save Close : {}", closeDTO);
        return closeRepository.save(closeMapper.toEntity(closeDTO).setIsPersisted()).map(closeMapper::toDto);
    }

    @Override
    public Mono<CloseDTO> partialUpdate(CloseDTO closeDTO) {
        log.debug("Request to partially update Close : {}", closeDTO);

        return closeRepository
            .findById(closeDTO.getId())
            .map(existingClose -> {
                closeMapper.partialUpdate(existingClose, closeDTO);

                return existingClose;
            })
            .flatMap(closeRepository::save)
            .map(closeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CloseDTO> findAll() {
        log.debug("Request to get all Closes");
        return closeRepository.findAll().map(closeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return closeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CloseDTO> findOne(String id) {
        log.debug("Request to get Close : {}", id);
        return closeRepository.findById(id).map(closeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Close : {}", id);
        return closeRepository.deleteById(id);
    }
}
