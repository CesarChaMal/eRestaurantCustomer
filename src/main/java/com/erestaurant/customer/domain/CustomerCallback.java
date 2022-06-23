package com.erestaurant.customer.domain;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.r2dbc.mapping.event.AfterSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerCallback implements AfterSaveCallback<Customer>, AfterConvertCallback<Customer> {

    @Override
    public Publisher<Customer> onAfterConvert(Customer entity, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }

    @Override
    public Publisher<Customer> onAfterSave(Customer entity, OutboundRow outboundRow, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }
}
