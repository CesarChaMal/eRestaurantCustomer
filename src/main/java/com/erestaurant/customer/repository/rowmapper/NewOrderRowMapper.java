package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.NewOrder;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link NewOrder}, with proper type conversions.
 */
@Service
public class NewOrderRowMapper implements BiFunction<Row, String, NewOrder> {

    private final ColumnConverter converter;

    public NewOrderRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link NewOrder} stored in the database.
     */
    @Override
    public NewOrder apply(Row row, String prefix) {
        NewOrder entity = new NewOrder();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setEnabled(converter.fromRow(row, prefix + "_enabled", Boolean.class));
        return entity;
    }
}
