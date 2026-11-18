package com.alexkrug.files_cloud_storage.resources.api;


import com.alexkrug.files_cloud_storage.resources.api.dto.DirectoryDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.resources.application.DirectoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/directory")
@Validated
public class DirectoryController {
    private final DirectoryService directoryService;

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

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
