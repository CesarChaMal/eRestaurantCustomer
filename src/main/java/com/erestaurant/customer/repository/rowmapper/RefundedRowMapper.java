package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.Refunded;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Refunded}, with proper type conversions.
 */
@Service
public class RefundedRowMapper implements BiFunction<Row, String, Refunded> {

    private final ColumnConverter converter;

    public RefundedRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Refunded} stored in the database.
     */
    @Override
    public Refunded apply(Row row, String prefix) {
        Refunded entity = new Refunded();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setEnabled(converter.fromRow(row, prefix + "_enabled", Boolean.class));
        return entity;
    }
}
