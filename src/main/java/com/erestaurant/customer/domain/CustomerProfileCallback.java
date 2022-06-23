package com.erestaurant.customer.domain;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.r2dbc.mapping.event.AfterSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerProfileCallback implements AfterSaveCallback<CustomerProfile>, AfterConvertCallback<CustomerProfile> {

    @Override
    public Publisher<CustomerProfile> onAfterConvert(CustomerProfile entity, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }

    @Override
    public Publisher<CustomerProfile> onAfterSave(CustomerProfile entity, OutboundRow outboundRow, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }
}
