package com.leep.security.edge.tracing.service;

import com.leep.security.edge.tracing.model.Severity;
import org.springframework.stereotype.Component;

@Component
public class SeverityEvaluator {

    public Severity evaluate(String area, boolean nearLimit, boolean hasError) {
        if (hasError) return Severity.ERROR;
        if ("admin".equalsIgnoreCase(area)) return nearLimit ? Severity.CRITICAL : Severity.WARN;
        if (nearLimit) return Severity.WARN;
        return Severity.INFO;
    }
}