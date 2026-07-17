package com.deep.payload.response;

import com.deep.payload.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String jwt;
    private String message;
    private String title;
    private UserDTO user;
}
