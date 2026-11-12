package com.alexkrug.files_cloud_storage.resources.api;

import com.alexkrug.files_cloud_storage.resources.application.DirectoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/directory")
@Validated
public class DirectoryController {
    private final DirectoryService directoryService;
    private final Log logger = LogFactory.getLog(DirectoryController.class);

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> getDirectory(
            @RequestParam(name = "path")
            @Pattern(regexp = "^([^<>:\"/\\\\|?*\\x00-\\x1F]+/)+$", message = "Path must be like file1/file2/")
            String path,
            HttpSession session) {

        return ResponseEntity.ok().build();
    }

}
