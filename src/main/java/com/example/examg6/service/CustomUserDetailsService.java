package com.example.examg6.service;

import com.example.examg6.entity.User;
import com.example.examg6.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsernameOrEmailAndActiveTrue(usernameOrEmail, usernameOrEmail);
        if(userOptional.isPresent() && userOptional.get().getActive()) {
            User user = userOptional.get();
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities(user.getRoles().stream()
                            .map(role -> "ROLE_" + role.getName())
                            .collect(Collectors.toList())
                            .toArray(new String[0]))
                    .build();
        }else {
            throw new UsernameNotFoundException(usernameOrEmail);
        }
    }
}
