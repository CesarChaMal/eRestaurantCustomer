package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.Customer;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Customer}, with proper type conversions.
 */
@Service
public class CustomerRowMapper implements BiFunction<Row, String, Customer> {

    private final ColumnConverter converter;

    public CustomerRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Customer} stored in the database.
     */
    @Override
    public Customer apply(Row row, String prefix) {
        Customer entity = new Customer();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setImageContentType(converter.fromRow(row, prefix + "_image_content_type", String.class));
        entity.setImage(converter.fromRow(row, prefix + "_image", byte[].class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setAge(converter.fromRow(row, prefix + "_age", Integer.class));
        return entity;
    }
}
