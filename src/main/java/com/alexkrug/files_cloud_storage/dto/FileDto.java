package com.alexkrug.files_cloud_storage.dto;

import com.alexkrug.files_cloud_storage.entity.ResourceType;

public record FileDto (
        String path,
        String name,
        Long size,
        ResourceType type
) implements ResourceDto {}
