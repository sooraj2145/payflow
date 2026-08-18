package com.sooraj.payflow.controller;


import com.sooraj.payflow.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        if("payflow-client".equals(request.clientId()) && "payflow-secret".equals(request.clientSecret())){
            String token = jwtService.generateToken(request.clientId());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    public record LoginRequest(String clientId, String clientSecret) {
    }
}
