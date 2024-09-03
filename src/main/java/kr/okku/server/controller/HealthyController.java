package kr.okku.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class HealthyController {

    @GetMapping("/healthy")
    public ResponseEntity<Void> checkHealth() {
        return ResponseEntity.ok().build();
    }
}
