package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.Close;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Close}, with proper type conversions.
 */
@Service
public class CloseRowMapper implements BiFunction<Row, String, Close> {

    private final ColumnConverter converter;

    public CloseRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Close} stored in the database.
     */
    @Override
    public Close apply(Row row, String prefix) {
        Close entity = new Close();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setEnabled(converter.fromRow(row, prefix + "_enabled", Boolean.class));
        return entity;
    }
}
