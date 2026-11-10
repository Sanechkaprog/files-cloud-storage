package com.alexkrug.files_cloud_storage.auth.api;

import com.alexkrug.files_cloud_storage.auth.api.dto.MainResponse;
import com.alexkrug.files_cloud_storage.auth.application.UserService;
import com.alexkrug.files_cloud_storage.auth.domain.User;
import com.alexkrug.files_cloud_storage.auth.exceptions.AuthException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("me")
    public ResponseEntity<MainResponse> getUser(HttpSession httpSession) {
        if (httpSession == null || httpSession.getAttribute("userId") == null) {
            throw new AuthException("You can`t use this method, because you`re unauthorized");
        }

        UUID userId = (UUID) httpSession.getAttribute("userId");

        User user = userService.getUserById(userId);

        return new ResponseEntity<>(
                new MainResponse(user.getUsername()),
                HttpStatus.OK
        );
    }
}
