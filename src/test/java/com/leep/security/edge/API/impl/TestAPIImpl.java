package com.leep.security.edge.API.impl;


import com.leep.security.edge.API.TestAPI;
import com.leep.security.edge.rateLimiting.annotation.RateLimited;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAPIImpl implements TestAPI {
    @Override
    @GetMapping("/findAll/notRateLimited")
    public String find() {
        System.out.println("TestAPIImpl.find non limitata");
        return "Ciao, " + " (non validato)";
    }

    @Override
    public String findById(String name) {
        System.out.println("TestAPIImpl.findById non limitata");

        return "Ciao, " + name + " (validato)";
    }

    @Override
    @RateLimited(limit = 3, durationSeconds = 10)
    @GetMapping("/findAll/rateLimited")
    public String findLimited() {
        System.out.println("TestAPIImpl.find non limitata");
        return "Ciao, " + " (non validato)";
    }

    @Override
    @GetMapping("/findById/validatedAndRateLimited")
    @RateLimited(limit = 3, durationSeconds = 10)
    public ResponseEntity<String> findLimitedAndValidated(String name) {
        if (name.length() > 50) {
            return new ResponseEntity<>("Limite superato", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Limite validato", HttpStatus.OK);
    }

}
