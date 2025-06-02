package com.leep.security.hedge.API.impl;


import com.leep.security.hedge.API.TestAPI;
import com.leep.security.hedge.rateLimiting.annotation.RateLimited;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAPIImpl implements TestAPI {

    @GetMapping("/findAll/notRateLimited")
    @Override
    public ResponseEntity<String> find() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }

    @GetMapping("/findAll/rateLimited")
    @Override
    @RateLimited(limit = 3, durationSeconds = 10)
    public ResponseEntity<String> findLimited() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }


}
