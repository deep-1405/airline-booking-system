package com.deep.controller;

import com.deep.payload.dto.UserDTO;
import com.deep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

//    @GetMapping("")
//    public ResponseEntity<UserDTO> getUserProfile(@Valid @RequestParam String email) throws Exception {
//        User user = userService.getUserByEmail(email);
//        return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toDTO(user));
//    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(
            @RequestHeader("X-User-Email") String email
    ) throws Exception {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable long userId
    ) throws Exception {
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUser() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }
}
