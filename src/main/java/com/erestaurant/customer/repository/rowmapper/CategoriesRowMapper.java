package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.Categories;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Categories}, with proper type conversions.
 */
@Service
public class CategoriesRowMapper implements BiFunction<Row, String, Categories> {

    private final ColumnConverter converter;

    public CategoriesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Categories} stored in the database.
     */
    @Override
    public Categories apply(Row row, String prefix) {
        Categories entity = new Categories();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
