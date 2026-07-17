package com.deep.payload.dto;

import com.deep.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Example value on the swagger is generated from here, taking example of this
public class UserDTO {

//    @JsonIgnore
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;

    // this tells spring, you are allowed to receive a password during signup (write), but you are never allowed to include it in a JSON response (Read)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String username;

    @JsonIgnore
    private LocalDateTime lastLogin;
}
