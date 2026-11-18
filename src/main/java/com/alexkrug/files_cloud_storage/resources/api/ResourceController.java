package com.alexkrug.files_cloud_storage.resources.api;

import com.alexkrug.files_cloud_storage.resources.api.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.resources.application.ResourceService;
import com.alexkrug.files_cloud_storage.resources.tools.PathResolver;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.nio.charset.StandardCharsets;
import java.util.List;


@RestController()
@RequestMapping(value = "/api/resource")
@Validated
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ResourceDto>> upload(

            @RequestParam("path") String path,

            @RequestParam("object") List<MultipartFile> files,
            HttpSession httpSession

            ) {
            String modifiedPath = httpSession.getAttribute("userId") + "/" + path;

        return new ResponseEntity<>(
                resourceService.upload(modifiedPath, files),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResourceDto>> searchResources(
            @NotEmpty
            @RequestParam("query") String query
    ) {
        return new ResponseEntity<>(
                resourceService.search(query),
                HttpStatus.OK
        );
    }


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

    @DeleteMapping()
    public ResponseEntity<HttpStatus> delete(
            @RequestParam("path") String path,
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

    @GetMapping()
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


    @PostMapping("/move")
    public ResponseEntity<ResourceDto> move(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
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
