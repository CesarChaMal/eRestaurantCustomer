package com.erestaurant.customer.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.erestaurant.customer.domain.Categories;
import com.erestaurant.customer.repository.rowmapper.CategoriesRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Categories entity.
 */
@SuppressWarnings("unused")
class CategoriesRepositoryInternalImpl extends SimpleR2dbcRepository<Categories, String> implements CategoriesRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CategoriesRowMapper categoriesMapper;

    private static final Table entityTable = Table.aliased("categories", EntityManager.ENTITY_ALIAS);

    public CategoriesRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategoriesRowMapper categoriesMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Categories.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.categoriesMapper = categoriesMapper;
    }

    @Override
    public Flux<Categories> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Categories> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CategoriesSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Categories.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Categories> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Categories> findById(String id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Categories process(Row row, RowMetadata metadata) {
        Categories entity = categoriesMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Categories> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
