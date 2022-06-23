package com.erestaurant.customer.domain;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.r2dbc.mapping.event.AfterSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RefundedCallback implements AfterSaveCallback<Refunded>, AfterConvertCallback<Refunded> {

    @Override
    public Publisher<Refunded> onAfterConvert(Refunded entity, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }

    @Override
    public Publisher<Refunded> onAfterSave(Refunded entity, OutboundRow outboundRow, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }
}
