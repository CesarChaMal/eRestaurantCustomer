package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.Complete;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Complete}, with proper type conversions.
 */
@Service
public class CompleteRowMapper implements BiFunction<Row, String, Complete> {

    private final ColumnConverter converter;

    public CompleteRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Complete} stored in the database.
     */
    @Override
    public Complete apply(Row row, String prefix) {
        Complete entity = new Complete();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setEnabled(converter.fromRow(row, prefix + "_enabled", Boolean.class));
        return entity;
    }
}
