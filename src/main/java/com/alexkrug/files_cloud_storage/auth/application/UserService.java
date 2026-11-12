package com.alexkrug.files_cloud_storage.auth.application;

import com.alexkrug.files_cloud_storage.auth.domain.User;
import com.alexkrug.files_cloud_storage.auth.exceptions.InvalidCredentialsException;
import com.alexkrug.files_cloud_storage.auth.exceptions.NicknameIsTakenException;
import com.alexkrug.files_cloud_storage.auth.infrastructure.UserRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;


@Service
@Transactional
public class UserService  {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final AuthenticationManager authenticationManager;
    private final MinioClient minioClient;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, MinioClient minioClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.minioClient = minioClient;
    }

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
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("cloud-storage")
                            .object( uuid + "/")
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException();
        }

        logger.info("User was saved in db");
        return user.getUsername();
    }

    public Authentication signIn(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            logger.info("Authentication passed");
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
