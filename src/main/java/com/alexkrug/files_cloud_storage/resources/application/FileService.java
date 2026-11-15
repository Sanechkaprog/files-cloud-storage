package com.alexkrug.files_cloud_storage.resources.application;

import com.alexkrug.files_cloud_storage.resources.api.dto.FileDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceRecord;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceType;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderIsAlreadyExistException;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderNotExistsException;
import com.alexkrug.files_cloud_storage.resources.infrastucture.ResourceRepository;
import com.alexkrug.files_cloud_storage.storage.service.ObjectStorageService;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    private final DirectoryService directoryService;
    private final Log log = LogFactory.getLog(FileService.class);
    private final ObjectStorageService objectStorageService;
    private final ResourceRepository resourceRepository;

    public FileService(DirectoryService directoryService, ObjectStorageService objectStorageService, ResourceRepository resourceRepository) {
        this.directoryService = directoryService;
        this.objectStorageService = objectStorageService;
        this.resourceRepository = resourceRepository;
    }

    public List<ResourceDto> upload(String path, List<MultipartFile> multipartFiles) {
        if (!directoryService.isExistFolder(path)) {
            throw new FolderNotExistsException("Folder not exist");
        }
        String modifiedPath = path + "/";
        List<ResourceDto> dtos = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String filePath = modifiedPath + multipartFile.getOriginalFilename();
            dtos.addAll(directoryService.createDirectories(filePath));
            Long size = multipartFile.getSize();
            if (directoryService.isExistFolder(filePath)) {
                throw new FolderIsAlreadyExistException("File is already exist");
            }
            try (InputStream is = multipartFile.getInputStream()) {
                objectStorageService.createFile(filePath, is, size);
                FileDto fileDto = new FileDto(
                        filePath.substring(filePath.indexOf('/') + 1, filePath.lastIndexOf('/') + 1),
                        filePath.substring(filePath.lastIndexOf('/') + 1),
                        size,
                        ResourceType.FILE
                );
                dtos.add(fileDto);
                ResourceRecord resourceRecord = new ResourceRecord(
                        filePath.substring(0, filePath.lastIndexOf('/') + 1),
                        filePath.substring(filePath.lastIndexOf('/') + 1),
                        size,
                        ResourceType.FILE
                );
                resourceRepository.save(resourceRecord);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return dtos;
    }
}