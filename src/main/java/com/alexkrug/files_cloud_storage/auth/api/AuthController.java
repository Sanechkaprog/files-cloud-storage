package com.alexkrug.files_cloud_storage.auth.api;

import com.alexkrug.files_cloud_storage.auth.api.dto.MainResponse;
import com.alexkrug.files_cloud_storage.auth.api.dto.SignUpRequest;
import com.alexkrug.files_cloud_storage.auth.application.UserService;
import com.alexkrug.files_cloud_storage.auth.domain.User;
import com.alexkrug.files_cloud_storage.common.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication controller")

@RequestMapping(value = "/api/auth")
@RestController
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registration")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User is created",
                    content = @Content(schema = @Schema(implementation = MainResponse.class))
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Username is taken",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })

    @PostMapping(value = "sign-up")
    public ResponseEntity<MainResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        String username = signUpRequest.username();

        String password = signUpRequest.password();

        String usernameInDb = userService.signUp(username, password);

        MainResponse response = new MainResponse(usernameInDb);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Authorization")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Get authorized",
                    content = @Content(schema = @Schema(implementation = MainResponse.class))
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credential",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })

    @PostMapping("sign-in")
    public ResponseEntity<MainResponse> signIn(@Valid @RequestBody SignUpRequest signUpRequest, HttpSession session) {
        String username = signUpRequest.username();

        String password = signUpRequest.password();

        Authentication authentication = userService.signIn(username, password);

        String usernameAuth = authentication.getName();

        User user = userService.getUserByUsername(usernameAuth);

        session.setAttribute("userId", user.getUserId());

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        MainResponse mainResponse = new MainResponse(authentication.getName());

        return new ResponseEntity<>(mainResponse, HttpStatus.OK);
    }


    @Operation(summary = "Log out")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Success logging out",
                    content = @Content
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

    @PostMapping("sign-out")
    public ResponseEntity<HttpStatus> signOut(HttpSession session, HttpServletResponse httpServletResponse) {
        session.invalidate();

        Cookie cookie = new Cookie("JSESSIONID", null);

        cookie.setPath("/");

        cookie.setMaxAge(0);

        httpServletResponse.addCookie(cookie);

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();

    }
}
