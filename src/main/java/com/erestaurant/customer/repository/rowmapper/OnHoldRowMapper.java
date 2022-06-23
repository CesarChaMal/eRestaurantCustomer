package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.OnHold;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OnHold}, with proper type conversions.
 */
@Service
public class OnHoldRowMapper implements BiFunction<Row, String, OnHold> {

    private final ColumnConverter converter;

    public OnHoldRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OnHold} stored in the database.
     */
    @Override
    public OnHold apply(Row row, String prefix) {
        OnHold entity = new OnHold();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setEnabled(converter.fromRow(row, prefix + "_enabled", Boolean.class));
        return entity;
    }
}
