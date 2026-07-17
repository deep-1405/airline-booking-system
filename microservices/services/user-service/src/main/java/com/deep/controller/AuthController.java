package com.deep.controller;

import com.deep.payload.dto.UserDTO;
import com.deep.payload.request.LoginRequest;
import com.deep.payload.response.AuthResponse;
import com.deep.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid UserDTO userDTO) throws Exception {
        AuthResponse response = authService.signup(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) throws Exception {
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
