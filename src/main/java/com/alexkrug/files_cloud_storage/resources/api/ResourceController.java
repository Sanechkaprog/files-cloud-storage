package com.alexkrug.files_cloud_storage.resources.api;

import com.alexkrug.files_cloud_storage.common.dto.ErrorResponse;
import com.alexkrug.files_cloud_storage.resources.api.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.resources.application.ResourceService;
import com.alexkrug.files_cloud_storage.resources.tools.PathResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.nio.charset.StandardCharsets;
import java.util.List;


@RestController
@RequestMapping(value = "/api/resource")
@Validated
@Tag(name = "Resource controller")
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ResourceDto>> upload(
            @RequestParam("path")
            String path,

            @RequestParam("object") List<MultipartFile> files,
            HttpSession httpSession

            ) {
            String modifiedPath = httpSession.getAttribute("userId") + "/" + path;

        return new ResponseEntity<>(
                resourceService.upload(modifiedPath, files),
                HttpStatus.CREATED
        );
    }

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

    @GetMapping("/search")
    public ResponseEntity<List<ResourceDto>> searchResources(
            @RequestParam("query")
            String query
    ) {
        return new ResponseEntity<>(
                resourceService.search(query),
                HttpStatus.OK
        );
    }

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
    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> download(
            @RequestParam("path") String path,

            HttpSession httpSession
    ) {
        String modifiedPath;

        if (path.startsWith("/")) {
            modifiedPath = httpSession.getAttribute("userId") + path;
        } else {
            modifiedPath = httpSession.getAttribute("userId") + "/" + path;
        }

        boolean isDirectory = resourceService.isDirectory(modifiedPath);

        String rawPath = PathResolver.getPathWithoutSuffix("/");

        String resourceName = PathResolver.getFileName(rawPath);

        if (isDirectory) {
            resourceName += ".zip";
        }

        StreamingResponseBody responseBody =
                resourceService.download(modifiedPath);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        "Content-Disposition",
                        ContentDisposition.attachment()
                                .filename(resourceName, StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(responseBody);
    }

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

    @DeleteMapping
    public ResponseEntity<HttpStatus> delete(
            @RequestParam("path")
            String path,
            HttpSession session
    ) {
        String modifiedPath;

        if (path.startsWith("/")) {
            modifiedPath = session.getAttribute("userId") + path;
        } else {
            modifiedPath = session.getAttribute("userId") + "/" + path;
        }

        resourceService.deleteResource(modifiedPath);

        return ResponseEntity.ok(
                HttpStatus.NO_CONTENT
        );
    }

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
    @GetMapping
    public ResponseEntity<ResourceDto> getResource(
            @RequestParam("path") String path,
            HttpSession session
    ) {

        String modifiedPath;

        if (path.startsWith("/")) {
            modifiedPath = session.getAttribute("userId") + path;
        } else {
            modifiedPath = session.getAttribute("userId") + "/" + path;
        }


        return new ResponseEntity<>(
                resourceService.getResource(modifiedPath),
                HttpStatus.OK
        );
    }


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

    @PostMapping("/move")
    public ResponseEntity<ResourceDto> move(
            @RequestParam("from")
            String from,

            @RequestParam("to")
            String to,

            HttpSession httpSession
    ) {
        String modifiedFrom;
        String modifiedTo;

        String userId = String.valueOf(
                httpSession.getAttribute("userId")
        );

        if (from.startsWith("/")) {
            modifiedFrom = userId  + from;
        } else {
            modifiedFrom = userId + "/" + from;
        }

        if (to.startsWith("/")) {
            modifiedTo = userId + to;
        } else {
            modifiedTo = userId + "/" + to;
        }

        ResourceDto resourceDto = resourceService.moveResource(
                modifiedFrom,
                modifiedTo
        );

        return ResponseEntity.ok(resourceDto);
    }

}
