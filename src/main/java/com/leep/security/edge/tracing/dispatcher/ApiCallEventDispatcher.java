package com.leep.security.edge.tracing.dispatcher;


import com.leep.security.edge.tracing.model.ApiCallEvent;

public interface ApiCallEventDispatcher {
    void dispatch(ApiCallEvent event);
}