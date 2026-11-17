package com.alexkrug.files_cloud_storage.resources.api;

import com.alexkrug.files_cloud_storage.common.tools.PathChecker;
import com.alexkrug.files_cloud_storage.resources.api.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.resources.application.FileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
    private final FileService fileService;

    public ResourceController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ResourceDto>> upload(

            @Pattern(regexp = "^([^<>:\"/\\\\|?*\\x00-\\x1F]+/)*[^<>:\"/\\\\|?*\\x00-\\x1F]+$",
                    message = "Path must be like file1/file2/file3")
            @RequestParam("path") String path,

            @RequestParam("files") List<MultipartFile> files,
            HttpSession httpSession

            ) {
            String modifiedPath = httpSession.getAttribute("userId") + "/" + path;

        return new ResponseEntity<>(
                fileService.upload(modifiedPath, files),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResourceDto>> searchResources(
            @NotEmpty
            @RequestParam("query") String query
    ) {
        return new ResponseEntity<>(
                fileService.search(query),
                HttpStatus.OK
        );
    }


    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> download(

            @RequestParam("path") String path,

            HttpSession httpSession
    ) {

        if (PathChecker.isInvalidPath(path)) {
            throw new IllegalStateException("Invalid path");
        }

        String modifiedPath = httpSession.getAttribute("userId") + "/" + path;

        boolean isDirectory = fileService.isDirectory(modifiedPath);

        String filename = path.substring(path.lastIndexOf('/') + 1);

        if (isDirectory) {
            filename += ".zip";
        }

        StreamingResponseBody responseBody =
                fileService.download(modifiedPath);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        "Content-Disposition",
                        ContentDisposition.attachment()
                                .filename(filename, StandardCharsets.UTF_8)
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
        String modifiedPath = session.getAttribute("userId") + "/" + path;

        if (PathChecker.isInvalidPath(modifiedPath)) {
            throw new IllegalStateException("Invalid path");
        }

        fileService.deleteResource(modifiedPath);
        return ResponseEntity.ok(
                HttpStatus.NO_CONTENT
        );
    }

    @GetMapping()
    public ResponseEntity<ResourceDto> getResource(
            @RequestParam("path") String path,
            HttpSession session
    ) {
        String modifiedPath = session.getAttribute("userId") + "/" + path;

        if (PathChecker.isInvalidPath(path)) {
           throw new IllegalStateException("Invalid path");
        }

        return new ResponseEntity<>(
                fileService.getResource(modifiedPath),
                HttpStatus.OK
        );

    }
}
