package com.alexkrug.files_cloud_storage.controller;

import com.alexkrug.files_cloud_storage.doc.DirectoryApi;
import com.alexkrug.files_cloud_storage.dto.DirectoryDto;
import com.alexkrug.files_cloud_storage.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.service.DirectoryService;
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
@RequestMapping(value = "/api/directory")
public class DirectoryController implements DirectoryApi {
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
