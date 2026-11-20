package com.alexkrug.files_cloud_storage.doc;

import com.alexkrug.files_cloud_storage.dto.ErrorResponse;
import com.alexkrug.files_cloud_storage.dto.MainResponse;
import com.alexkrug.files_cloud_storage.dto.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication controller")
public interface AuthApi {
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

    ResponseEntity<MainResponse> signUp(SignUpRequest signUpRequest);


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

    ResponseEntity<MainResponse> signIn(SignUpRequest signUpRequest, HttpSession session);

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

    ResponseEntity<HttpStatus> signOut(HttpSession session, HttpServletResponse httpServletResponse);



}
