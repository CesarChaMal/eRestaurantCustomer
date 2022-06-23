package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.Cart;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Cart}, with proper type conversions.
 */
@Service
public class CartRowMapper implements BiFunction<Row, String, Cart> {

    private final ColumnConverter converter;

    public CartRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Cart} stored in the database.
     */
    @Override
    public Cart apply(Row row, String prefix) {
        Cart entity = new Cart();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
