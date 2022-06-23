package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.Cancel;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Cancel}, with proper type conversions.
 */
@Service
public class CancelRowMapper implements BiFunction<Row, String, Cancel> {

    private final ColumnConverter converter;

    public CancelRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Cancel} stored in the database.
     */
    @Override
    public Cancel apply(Row row, String prefix) {
        Cancel entity = new Cancel();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setEnabled(converter.fromRow(row, prefix + "_enabled", Boolean.class));
        return entity;
    }
}
