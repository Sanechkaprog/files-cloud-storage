package com.alexkrug.files_cloud_storage;

import com.alexkrug.files_cloud_storage.auth.application.UserService;
import com.alexkrug.files_cloud_storage.auth.exceptions.InvalidCredentialsException;
import com.alexkrug.files_cloud_storage.auth.exceptions.NicknameIsTakenException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UserServiceTest {

    private final UserService userService;

    @Autowired
    public UserServiceTest(UserService userService) {
        this.userService = userService;
    }

    @Test
    public void userRegistrationTest() {
        String username = "username";
        String password = "password";
        String usernameDb  = userService.signUp(username, password);
        assertEquals(username, usernameDb);
    }

    @Test
    public void registrationExceptionUsernameIsTakenTest() {
        String username = "user";
        String password = "mypassword";
        userService.signUp(username, password);
        assertThrows(NicknameIsTakenException.class, () -> userService.signUp(username, password));
    }

    @Test
    public void authorizationTest() {
        String username = "username1";
        String password = "password1";
        userService.signUp(username, password);
        Authentication authentication = userService.signIn(username, password);
        assertEquals(username, authentication.getName());
    }

    @Test
    public void authorizationInvalidCredentialsException() {
        String username = "username2";
        String password = "password2";
        userService.signUp(username, password);
        assertThrows(InvalidCredentialsException.class, () ->
                userService.signIn(username, "different_password"));
    }
}
