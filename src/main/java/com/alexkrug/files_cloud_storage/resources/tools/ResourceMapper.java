package com.alexkrug.files_cloud_storage.resources.tools;
import com.alexkrug.files_cloud_storage.resources.api.dto.DirectoryDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.FileDto;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(source = "pathWithoutPrefix", target = "path")
    DirectoryDto toDirectoryDto(ResourceRecord resourceRecord, String pathWithoutPrefix);

    @Mapping(source = "pathWithoutPrefix", target = "path")
    FileDto toFileDto(ResourceRecord resourceRecord, String pathWithoutPrefix);
}
