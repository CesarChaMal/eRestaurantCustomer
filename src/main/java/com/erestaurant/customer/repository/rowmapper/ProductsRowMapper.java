package com.erestaurant.customer.repository.rowmapper;

import com.erestaurant.customer.domain.Products;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Products}, with proper type conversions.
 */
@Service
public class ProductsRowMapper implements BiFunction<Row, String, Products> {

    private final ColumnConverter converter;

    public ProductsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Products} stored in the database.
     */
    @Override
    public Products apply(Row row, String prefix) {
        Products entity = new Products();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setImageContentType(converter.fromRow(row, prefix + "_image_content_type", String.class));
        entity.setImage(converter.fromRow(row, prefix + "_image", byte[].class));
        entity.setEstimatedPreparaingTime(converter.fromRow(row, prefix + "_estimated_preparaing_time", Float.class));
        return entity;
    }
}
