package com.leep.security.edge.API.impl;


import com.leep.security.edge.API.TestAPI;
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
    public ResponseEntity<String> findLimited() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }


}
