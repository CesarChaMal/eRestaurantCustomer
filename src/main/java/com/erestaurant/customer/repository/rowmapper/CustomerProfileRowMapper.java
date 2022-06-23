package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.CustomerProfile;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CustomerProfile}, with proper type conversions.
 */
@Service
public class CustomerProfileRowMapper implements BiFunction<Row, String, CustomerProfile> {

    private final ColumnConverter converter;

    public CustomerProfileRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CustomerProfile} stored in the database.
     */
    @Override
    public CustomerProfile apply(Row row, String prefix) {
        CustomerProfile entity = new CustomerProfile();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setLocation(converter.fromRow(row, prefix + "_location", String.class));
        entity.setLocationRange(converter.fromRow(row, prefix + "_location_range", String.class));
        entity.setReferals(converter.fromRow(row, prefix + "_referals", String.class));
        return entity;
    }
}
