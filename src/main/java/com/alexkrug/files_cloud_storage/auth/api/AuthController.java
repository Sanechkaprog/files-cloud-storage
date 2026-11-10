package com.alexkrug.files_cloud_storage.auth.api;


import com.alexkrug.files_cloud_storage.auth.api.dto.MainResponse;
import com.alexkrug.files_cloud_storage.auth.api.dto.SignUpRequest;
import com.alexkrug.files_cloud_storage.auth.application.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("sign-up")
    public ResponseEntity<MainResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        String username = signUpRequest.username();
        String password = signUpRequest.password();

        String usernameInDb = userService.signUp(username, password);

        MainResponse response = new MainResponse(usernameInDb);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("sign-in")
    public ResponseEntity<MainResponse> signIn(@Valid @RequestBody SignUpRequest signUpRequest) {
        String username = signUpRequest.username();
        String password = signUpRequest.password();

        Authentication authentication = userService.signIn(username, password);
        MainResponse mainResponse = new MainResponse(authentication.getName());

        return new ResponseEntity<>(mainResponse, HttpStatus.OK);
    }
}

