package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.Order;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Order}, with proper type conversions.
 */
@Service
public class OrderRowMapper implements BiFunction<Row, String, Order> {

    private final ColumnConverter converter;

    public OrderRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Order} stored in the database.
     */
    @Override
    public Order apply(Row row, String prefix) {
        Order entity = new Order();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Float.class));
        return entity;
    }
}
