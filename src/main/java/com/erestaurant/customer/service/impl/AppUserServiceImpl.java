package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.AppUser;
import com.erestaurant.customer.repository.AppUserRepository;
import com.erestaurant.customer.service.AppUserService;
import com.erestaurant.customer.service.dto.AppUserDTO;
import com.erestaurant.customer.service.mapper.AppUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link AppUser}.
 */
@Service
@Transactional
public class AppUserServiceImpl implements AppUserService {

    private final Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);

    private final AppUserRepository appUserRepository;

    private final AppUserMapper appUserMapper;

    public AppUserServiceImpl(AppUserRepository appUserRepository, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
    }

    @Override
    public Mono<AppUserDTO> save(AppUserDTO appUserDTO) {
        log.debug("Request to save AppUser : {}", appUserDTO);
        return appUserRepository.save(appUserMapper.toEntity(appUserDTO)).map(appUserMapper::toDto);
    }

    @Override
    public Mono<AppUserDTO> update(AppUserDTO appUserDTO) {
        log.debug("Request to save AppUser : {}", appUserDTO);
        return appUserRepository.save(appUserMapper.toEntity(appUserDTO).setIsPersisted()).map(appUserMapper::toDto);
    }

    @Override
    public Mono<AppUserDTO> partialUpdate(AppUserDTO appUserDTO) {
        log.debug("Request to partially update AppUser : {}", appUserDTO);

        return appUserRepository
            .findById(appUserDTO.getId())
            .map(existingAppUser -> {
                appUserMapper.partialUpdate(existingAppUser, appUserDTO);

                return existingAppUser;
            })
            .flatMap(appUserRepository::save)
            .map(appUserMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AppUserDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AppUsers");
        return appUserRepository.findAllBy(pageable).map(appUserMapper::toDto);
    }

    public Flux<AppUserDTO> findAllWithEagerRelationships(Pageable pageable) {
        return appUserRepository.findAllWithEagerRelationships(pageable).map(appUserMapper::toDto);
    }

    public Mono<Long> countAll() {
        return appUserRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AppUserDTO> findOne(String id) {
        log.debug("Request to get AppUser : {}", id);
        return appUserRepository.findOneWithEagerRelationships(id).map(appUserMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete AppUser : {}", id);
        return appUserRepository.deleteById(id);
    }
}
