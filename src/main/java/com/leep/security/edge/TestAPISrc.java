package com.leep.security.edge;


import com.leep.security.edge.injection.SQLInjectionControl;
import com.leep.security.edge.rateLimiting.annotation.RateLimited;
import com.leep.security.edge.rateLimiting.model.enumeration.RateLimitType;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/test")
public class TestAPISrc {

    @GetMapping("/findAll/notRateLimited")
    public ResponseEntity<String> find() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }

    @GetMapping("/findAll/rateLimited")
    @RateLimited(limit = 3, durationSeconds = 10, type = RateLimitType.FIXED_WINDOW)
    @SQLInjectionControl(customMaxChar = 20, logLevel = LogLevel.INFO)
    public ResponseEntity<String> findLimited() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }

    @GetMapping("/findAll/controlled/{id}")
    @RateLimited(limit = 3, durationSeconds = 10, type = RateLimitType.FIXED_WINDOW)
    @SQLInjectionControl(maxChar = SQLInjectionControl.UserValidation.UUID, logLevel = LogLevel.INFO)
    public ResponseEntity<String> getControlled(@PathVariable("id") String id) {
        return new ResponseEntity<>(id, HttpStatus.OK);
    }


}
