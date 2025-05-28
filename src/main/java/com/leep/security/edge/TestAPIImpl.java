package com.leep.security.edge;


import com.leep.security.edge.rateLimiting.annotation.RateLimited;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/test")
public class TestAPIImpl {

    @GetMapping("/findAll/notRateLimited")
    public ResponseEntity<String> find() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }

    @GetMapping("/findAll/rateLimited")
    @RateLimited(limit = 3, durationSeconds = 10)
    public ResponseEntity<String> findLimited() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }


}
