package com.kapos.hypedemo.security;

import com.kapos.hypedemo.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserDetailsService {
    Optional<User> loadUserByUsername(String username)
            throws UsernameNotFoundException;
}
