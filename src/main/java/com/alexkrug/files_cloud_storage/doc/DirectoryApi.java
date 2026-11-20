package com.alexkrug.files_cloud_storage.doc;

import com.alexkrug.files_cloud_storage.dto.DirectoryDto;
import com.alexkrug.files_cloud_storage.dto.ErrorResponse;
import com.alexkrug.files_cloud_storage.dto.ResourceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Tag(name = "Directory controller")
public interface DirectoryApi {
    @Operation(summary = "Directory creating")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Directory is created",
                    content = @Content(schema = @Schema(implementation = DirectoryDto.class))
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "User is unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Parent-folder doesn`t exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Folder is already exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

    })

    ResponseEntity<DirectoryDto> setDirectory(String path, HttpSession session);

    @Operation(summary = "Directory creating")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Directory got",
                    content = @Content(schema = @Schema(implementation = ResourceDto.class))
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "User is unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Folder doesn`t exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),

    })

    ResponseEntity<List<ResourceDto>> getDirectory(String path, HttpSession session);

}
