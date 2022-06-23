package com.erestaurant.customer.service.impl;

import com.erestaurant.customer.domain.Categories;
import com.erestaurant.customer.repository.CategoriesRepository;
import com.erestaurant.customer.service.CategoriesService;
import com.erestaurant.customer.service.dto.CategoriesDTO;
import com.erestaurant.customer.service.mapper.CategoriesMapper;
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
 * Service Implementation for managing {@link Categories}.
 */
@Service
@Transactional
public class CategoriesServiceImpl implements CategoriesService {

    private final Logger log = LoggerFactory.getLogger(CategoriesServiceImpl.class);

    private final CategoriesRepository categoriesRepository;

    private final CategoriesMapper categoriesMapper;

    public CategoriesServiceImpl(CategoriesRepository categoriesRepository, CategoriesMapper categoriesMapper) {
        this.categoriesRepository = categoriesRepository;
        this.categoriesMapper = categoriesMapper;
    }

    @Override
    public Mono<CategoriesDTO> save(CategoriesDTO categoriesDTO) {
        log.debug("Request to save Categories : {}", categoriesDTO);
        return categoriesRepository.save(categoriesMapper.toEntity(categoriesDTO)).map(categoriesMapper::toDto);
    }

    @Override
    public Mono<CategoriesDTO> update(CategoriesDTO categoriesDTO) {
        log.debug("Request to save Categories : {}", categoriesDTO);
        return categoriesRepository.save(categoriesMapper.toEntity(categoriesDTO).setIsPersisted()).map(categoriesMapper::toDto);
    }

    @Override
    public Mono<CategoriesDTO> partialUpdate(CategoriesDTO categoriesDTO) {
        log.debug("Request to partially update Categories : {}", categoriesDTO);

        return categoriesRepository
            .findById(categoriesDTO.getId())
            .map(existingCategories -> {
                categoriesMapper.partialUpdate(existingCategories, categoriesDTO);

                return existingCategories;
            })
            .flatMap(categoriesRepository::save)
            .map(categoriesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CategoriesDTO> findAll() {
        log.debug("Request to get all Categories");
        return categoriesRepository.findAll().map(categoriesMapper::toDto);
    }

    public Mono<Long> countAll() {
        return categoriesRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CategoriesDTO> findOne(String id) {
        log.debug("Request to get Categories : {}", id);
        return categoriesRepository.findById(id).map(categoriesMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Categories : {}", id);
        return categoriesRepository.deleteById(id);
    }
}
