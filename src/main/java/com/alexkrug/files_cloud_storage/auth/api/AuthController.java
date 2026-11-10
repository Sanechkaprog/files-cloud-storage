package com.alexkrug.files_cloud_storage.auth.api;


import com.alexkrug.files_cloud_storage.auth.api.dto.MainResponse;
import com.alexkrug.files_cloud_storage.auth.api.dto.SignUpRequest;
import com.alexkrug.files_cloud_storage.auth.application.UserService;
import com.alexkrug.files_cloud_storage.auth.domain.User;
import com.alexkrug.files_cloud_storage.auth.exceptions.AuthException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<MainResponse> signIn(@Valid @RequestBody SignUpRequest signUpRequest, HttpSession session) {
        String username = signUpRequest.username();
        String password = signUpRequest.password();

        Authentication authentication = userService.signIn(username, password);

        String usernameAuth = authentication.getName();
        User user = userService.getUserByUsername(username);
        session.setAttribute("userId", user.getUserId());

        MainResponse mainResponse = new MainResponse(authentication.getName());

        return new ResponseEntity<>(mainResponse, HttpStatus.OK);
    }

    @PostMapping("sign-out")
    public ResponseEntity<HttpStatus> signOut(HttpSession session, HttpServletResponse httpServletResponse) {
        if (session == null || session.getAttribute("userId") == null) {
            throw new AuthException("User not authorized");
        }
        session.invalidate();
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);

        return ResponseEntity.ok().build();

    }
}
