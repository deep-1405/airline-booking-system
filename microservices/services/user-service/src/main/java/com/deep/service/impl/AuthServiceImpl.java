package com.deep.service.impl;

import com.deep.Repository.UserRepository;
import com.deep.config.JwtProvider;
import com.deep.enums.UserRole;
import com.deep.mapper.UserMapper;
import com.deep.model.User;
import com.deep.payload.dto.UserDTO;
import com.deep.payload.response.AuthResponse;
import com.deep.service.AuthService;
import com.deep.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /*
    *  1. check if email already exists or not
    *  2. Encode password using BCrypt
    *  3. Save user in the database
    *  4. Generate JWT token
    *  5. Return token and user information
    * */

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public AuthResponse signup(UserDTO req) throws Exception {

        // Step 1:
        // can not call orElseThrow on this because, it can only be called on the optional fields
        User existingUser = userRepository.findByEmail(req.getEmail());
        if(existingUser != null) {
            throw new Exception("Email already registered");
        }
        if(req.getRole() == UserRole.ROLE_SYSTEM_ADMIN) {
            throw new Exception("You can not sign up as system admin");
        }

        User createdUser = new User();
//        createdUser.setId(req.getId());
        createdUser.setFullName(req.getFullName());
        createdUser.setEmail(req.getEmail());
        createdUser.setPhone(req.getPhone());
        createdUser.setRole(req.getRole());
        createdUser.setUserName(req.getUsername());

        createdUser.setPassword(passwordEncoder.encode(req.getPassword()));

        createdUser.setLastLogin(LocalDateTime.now());
        createdUser.setVerified(false);

        User savedUser = userRepository.save(createdUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getEmail(),
                null,
                customUserDetailsService.loadUserByUsername(savedUser.getEmail()).getAuthorities()
        );

        String jwt = jwtProvider.generateToken(authentication, savedUser.getId());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setUser(UserMapper.toDTO(savedUser));
        authResponse.setTitle("Welcome " + savedUser.getFullName());
        authResponse.setMessage("Registered Successfully!");

        return authResponse;
    }

    /*
    *  1. Load user by email
    *  2. Compare password with Bcrypt
    *  3. Update `lastLogin` time
    *  4. Generate JWT token
    *  5. Return token and user information
    * */
    @Override
    public AuthResponse login(String email, String password) throws Exception {
        Authentication authentication = authenticate(email, password);

        User user = userRepository.findByEmail(email);
        user.setLastLogin(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        String jwt = jwtProvider.generateToken(authentication, user.getId());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setUser(UserMapper.toDTO(savedUser));
        authResponse.setTitle("Welcome back " + savedUser.getFullName());
        authResponse.setMessage("Logged in Successfully!");

        return authResponse;
    }


    private Authentication authenticate(String email, String password) throws Exception {

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if(!passwordEncoder.matches(
                password,
                userDetails.getPassword()
        )) {
            throw new Exception("Invalid password!");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
