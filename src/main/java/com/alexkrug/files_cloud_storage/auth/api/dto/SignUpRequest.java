package com.alexkrug.files_cloud_storage.auth.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpRequest (
        @Size(min=3, max=50, message = "username size must be between 3 and 50 symbols")
        @NotNull(message = "username can`t be empty")
        String username,

        @NotNull(message = "password can`t be empty")
        String password
){
}
