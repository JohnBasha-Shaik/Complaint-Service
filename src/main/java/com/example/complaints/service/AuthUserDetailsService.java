package com.example.complaints.service;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class AuthUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Placeholder: map usernames to roles. Passwords are bcrypt of "password"
        String passwordHash = "$2a$10$eB5rSL9kXMQgqQF0j7nUre5uJbphQ9U9d9Qv7r8I44wW4ycIbj9m2";
        if ("citizen1".equals(username)) {
            return new User(username, passwordHash, List.of(new SimpleGrantedAuthority("ROLE_CITIZEN")));
        }
        if ("staff1".equals(username)) {
            return new User(username, passwordHash, List.of(new SimpleGrantedAuthority("ROLE_STAFF")));
        }
        if ("admin1".equals(username)) {
            return new User(username, passwordHash, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }
        throw new UsernameNotFoundException("User not found");
    }
}