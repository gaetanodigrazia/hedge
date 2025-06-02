package com.leep.security.hedge.tracing.dispatcher;


import com.leep.security.hedge.tracing.model.ApiCallEvent;

public interface ApiCallEventDispatcher {
    void dispatch(ApiCallEvent event);
}