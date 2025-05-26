package com.leep.security.edge.API;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/public/test")
public interface TestAPI {
    ResponseEntity<String> find();

    ResponseEntity<String> findLimited();

}
