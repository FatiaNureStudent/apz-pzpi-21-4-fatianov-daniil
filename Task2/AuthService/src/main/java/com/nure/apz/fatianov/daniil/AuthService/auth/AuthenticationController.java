package com.nure.apz.fatianov.daniil.AuthService.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-service/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/is-admin")
    public ResponseEntity<Boolean> isAdmin(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {
        try{
            return ResponseEntity.ok().body(authenticationService.isAdmin(token));
        }catch(Exception ex){
            return ResponseEntity.badRequest().body(false);
        }

    }

    @GetMapping("/is-user")
    public ResponseEntity<Boolean> isUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {
        try{
            return ResponseEntity.ok().body(authenticationService.isUser(token));
        }catch(Exception ex){
            return ResponseEntity.badRequest().body(false);
        }

    }
}
