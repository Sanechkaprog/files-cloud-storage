package com.alexkrug.files_cloud_storage.resources.api;

import com.alexkrug.files_cloud_storage.resources.api.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.resources.application.FileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
}
