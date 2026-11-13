package com.alexkrug.files_cloud_storage.resources.api.dto;

import com.alexkrug.files_cloud_storage.resources.domain.ResourceType;

public record DirectoryDto (
        String path,
        String name,
        ResourceType type
) {
}
