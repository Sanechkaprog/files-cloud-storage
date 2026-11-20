package com.alexkrug.files_cloud_storage.doc;

import com.alexkrug.files_cloud_storage.dto.ErrorResponse;
import com.alexkrug.files_cloud_storage.dto.MainResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;

public interface UserApi {

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

    ResponseEntity<MainResponse> getUser(HttpSession httpSession);
}
