package com.alexkrug.files_cloud_storage.controller;

import com.alexkrug.files_cloud_storage.dto.MainResponse;
import com.alexkrug.files_cloud_storage.service.UserService;
import com.alexkrug.files_cloud_storage.entity.User;
import com.alexkrug.files_cloud_storage.exceptions.AuthException;
import com.alexkrug.files_cloud_storage.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@Tag(name = "User controller")
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registration")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Got user",
                    content = @Content(schema = @Schema(implementation = MainResponse.class))
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "User is unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })

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
