package com.deep.mapper;

import com.deep.config.SecurityConfig;
import com.deep.model.User;
import com.deep.payload.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserMapper {

    public static UserDTO toDTO(User user) {

        if(user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                // this is removed because this will available on the internet instead of only being in the database
                // .password(user.getPassword())
                .lastLogin(user.getLastLogin())
                .username(user.getUserName())
                .build();
    }

    public static List<UserDTO> toDTOList(List<User> user) {
        return user.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static Set<UserDTO> toDTOSet(Set<User> users) {
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toSet());
    }
}
