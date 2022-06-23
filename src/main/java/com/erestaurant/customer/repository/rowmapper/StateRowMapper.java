package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.State;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link State}, with proper type conversions.
 */
@Service
public class StateRowMapper implements BiFunction<Row, String, State> {

    private final ColumnConverter converter;

    public StateRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link State} stored in the database.
     */
    @Override
    public State apply(Row row, String prefix) {
        State entity = new State();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
