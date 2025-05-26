package com.leep.security.edge.API;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/public/test")
public interface TestAPI {
    String find();

    String findById(String id);

    String findLimited();

    ResponseEntity<String> findLimitedAndValidated(String name);
}
