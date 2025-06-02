package com.leep.security.hedge.tracing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ApiCallGraphController {

    @GetMapping("/api-call-graph")
    public Map<String, Set<String>> getMockGraph() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();

        graph.put("GET /login", Set.of("GET /user/me", "GET /user/roles"));
        graph.put("GET /user/me", Set.of("GET /permissions"));
        graph.put("POST /orders", Set.of("GET /inventory", "POST /payments"));
        graph.put("GET /dashboard", Set.of());

        return graph;
    }
}
