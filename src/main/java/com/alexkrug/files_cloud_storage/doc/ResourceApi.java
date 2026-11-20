package com.alexkrug.files_cloud_storage.doc;

import com.alexkrug.files_cloud_storage.dto.ErrorResponse;
import com.alexkrug.files_cloud_storage.dto.ResourceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@Tag(name = "Resource controller")
public interface ResourceApi {
    @Operation(summary = "Uploading")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Resources created",
                    content = @Content(schema = @Schema(implementation = ResourceDto.class))
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid body error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
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
            ),

    })

    ResponseEntity<List<ResourceDto>> upload(String path, List<MultipartFile> files, HttpSession httpSession);

    @Operation(summary = "Resource searching")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resource got",
                    content = @Content(schema = @Schema(implementation = ResourceDto.class))
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
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
            ),

    })

    ResponseEntity<List<ResourceDto>> searchResources(String query, HttpSession session);

    @Operation(summary = "Resource downloading")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resource got",
                    content = @Content(schema = @Schema(implementation = ResourceDto.class))
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid path",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "User is unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),


            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

    })

    ResponseEntity<StreamingResponseBody> download(String path, HttpSession httpSession);

    @Operation(summary = "Resource deleting")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Resource deleted",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid path",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "User is unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),


            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

    })

    ResponseEntity<HttpStatus> delete(String path, HttpSession session);

    @Operation(summary = "Resource info getting")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resource got",
                    content = @Content(schema = @Schema(implementation = ResourceDto.class))
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid path",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "User is unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),


            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

    })

    ResponseEntity<ResourceDto> getResource(String path, HttpSession session);

    @Operation(summary = "Resources moving")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Directory got",
                    content = @Content(schema = @Schema(implementation = ResourceDto.class))
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid path",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "User is unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Resource exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

    })

    ResponseEntity<ResourceDto> move(String from, String to, HttpSession httpSession);
}
