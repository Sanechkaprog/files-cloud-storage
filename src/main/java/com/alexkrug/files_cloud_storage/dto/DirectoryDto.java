package com.alexkrug.files_cloud_storage.dto;

import com.alexkrug.files_cloud_storage.entity.ResourceType;

public record DirectoryDto (
        String path,
        String name,
        ResourceType type
) implements ResourceDto {}
