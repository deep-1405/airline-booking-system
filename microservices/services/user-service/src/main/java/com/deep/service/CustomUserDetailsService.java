package com.deep.service;

import com.deep.Repository.UserRepository;
import com.deep.model.User;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new UsernameNotFoundException(email);
        }

        // In spring security, GrantedAuthority is an interface
        // a class like SimpleGrantedAuthority implements it
        // Converts your single UserRole enum (e.g. ADMIN) into Spring Security's authority format.
        // SimpleGrantedAuthority just wraps a string as a permission — Spring Security's convention is usually to prefix these with ROLE_ (e.g. ROLE_ADMIN)
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
        Collection<GrantedAuthority> grantedAuthorities = Collections.singletonList(
                authority
        );

        // This is a different User class than your entity — it's Spring Security's own built-in User class
        // (that's why it's fully qualified here, to disambiguate from com.deep.model.User).
        // It packages exactly what Spring Security needs to authenticate: the identifier (email), the hashed password (to compare against what the user typed), and the authorities/roles.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), grantedAuthorities
        );
    }
}
