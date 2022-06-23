package com.erestaurant.customer.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CustomerProfileSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("location", table, columnPrefix + "_location"));
        columns.add(Column.aliased("location_range", table, columnPrefix + "_location_range"));
        columns.add(Column.aliased("referals", table, columnPrefix + "_referals"));

        return columns;
    }
}
