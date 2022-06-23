package com.erestaurant.customer.domain;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.r2dbc.mapping.event.AfterSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CancelCallback implements AfterSaveCallback<Cancel>, AfterConvertCallback<Cancel> {

    @Override
    public Publisher<Cancel> onAfterConvert(Cancel entity, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }

    @Override
    public Publisher<Cancel> onAfterSave(Cancel entity, OutboundRow outboundRow, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }
}
