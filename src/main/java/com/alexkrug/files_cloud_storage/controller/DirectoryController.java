package com.alexkrug.files_cloud_storage.controller;

import com.alexkrug.files_cloud_storage.dto.ErrorResponse;
import com.alexkrug.files_cloud_storage.dto.DirectoryDto;
import com.alexkrug.files_cloud_storage.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.service.DirectoryService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@Tag(name = "Directory controller")
@RequestMapping(value = "/api/directory")
public class DirectoryController {
    private final DirectoryService directoryService;

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

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

    @PostMapping()
    public ResponseEntity<DirectoryDto> setDirectory(
            @RequestParam(name = "path")
            String path,
            HttpSession session) {

        String modifiedPath = session.getAttribute("userId") + "/" + path;

        return
                new ResponseEntity<>(
                        directoryService.addDirectory(modifiedPath),
                        HttpStatus.CREATED
                );

    }

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

    @GetMapping()
    public ResponseEntity<List<ResourceDto>> getDirectory(
            @RequestParam(name = "path")
            String path,
            HttpSession session
    ) {
        String modifiedPath = session.getAttribute("userId") + "/" + path;
        return new ResponseEntity<>(
                directoryService.getDirectoryResources(modifiedPath),
                HttpStatus.OK
        );
    }
}
