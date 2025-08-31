package com.francis.bookshop.service;

import static java.util.Collections.singletonList;

import com.francis.bookshop.entity.User;
import com.francis.bookshop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                             new UsernameNotFoundException("User not found with username: " + username)
            );

        // Prefix role with "ROLE_"
        String roleName = "ROLE_" + user.getRole().getName();

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}
