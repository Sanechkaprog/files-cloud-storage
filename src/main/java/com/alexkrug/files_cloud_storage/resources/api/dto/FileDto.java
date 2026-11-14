package com.alexkrug.files_cloud_storage.resources.api.dto;

import com.alexkrug.files_cloud_storage.resources.domain.ResourceType;

public record FileDto (
        String path,
        String name,
        Long size,
        ResourceType type
) implements ResourceDto {}
