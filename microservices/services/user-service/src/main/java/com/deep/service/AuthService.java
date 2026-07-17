package com.deep.service;

import com.deep.payload.response.AuthResponse;
import com.deep.payload.dto.UserDTO;

public interface AuthService {

    AuthResponse login(String email, String password) throws Exception;
    AuthResponse signup(UserDTO req) throws Exception;
}
