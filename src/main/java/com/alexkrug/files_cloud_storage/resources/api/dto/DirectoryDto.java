package com.alexkrug.files_cloud_storage.resources.api.dto;

public record DirectoryDto (
        String path,
        String name,
        String type
) {
}
