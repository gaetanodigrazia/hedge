package com.leep.security.hedge.injection;

import com.leep.security.hedge.injection.annotation.Injection;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @PostMapping
    @Injection(checkSql = true, checkOs = false, maxChar = 50)
    public String testEndpoint(@RequestBody String input) {
        return "Received: " + input;
    }
}
