package com.alexkrug.files_cloud_storage.service;

import com.alexkrug.files_cloud_storage.entity.User;
import com.alexkrug.files_cloud_storage.exceptions.InvalidCredentialsException;
import com.alexkrug.files_cloud_storage.exceptions.NicknameIsTakenException;
import com.alexkrug.files_cloud_storage.repository.UserRepository;
import com.alexkrug.files_cloud_storage.repository.ObjectStorage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserService  {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ObjectStorage objectStorage;

    private final AuthenticationManager authenticationManager;


    public String signUp(String username, String password) {
        if (userRepository.findUserByUsername(username).isPresent()) {
            throw new NicknameIsTakenException("Nickname is already taken");
        }
        User user = new User();
        var encodedPassword = passwordEncoder.encode(password);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        UUID uuid = user.getUserId();
        String initialBucketPath = uuid + "/";
        objectStorage.createDirectory(initialBucketPath);
        log.info("User was saved in db");
        return user.getUsername();
    }

    public Authentication signIn(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            log.info("Authentication passed");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;

        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }
    public User getUserById(UUID uuid) {
        return userRepository.getUserByUserId(uuid);
    }
}

