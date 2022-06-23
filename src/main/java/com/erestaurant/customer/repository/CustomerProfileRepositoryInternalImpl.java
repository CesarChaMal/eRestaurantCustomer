package com.erestaurant.customer.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.erestaurant.customer.domain.CustomerProfile;
import com.erestaurant.customer.repository.rowmapper.CustomerProfileRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the CustomerProfile entity.
 */
@SuppressWarnings("unused")
class CustomerProfileRepositoryInternalImpl
    extends SimpleR2dbcRepository<CustomerProfile, String>
    implements CustomerProfileRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerProfileRowMapper customerprofileMapper;

    private static final Table entityTable = Table.aliased("customer_profile", EntityManager.ENTITY_ALIAS);

    public CustomerProfileRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerProfileRowMapper customerprofileMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(CustomerProfile.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerprofileMapper = customerprofileMapper;
    }

    @Override
    public Flux<CustomerProfile> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<CustomerProfile> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CustomerProfileSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, CustomerProfile.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<CustomerProfile> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<CustomerProfile> findById(String id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private CustomerProfile process(Row row, RowMetadata metadata) {
        CustomerProfile entity = customerprofileMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends CustomerProfile> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
