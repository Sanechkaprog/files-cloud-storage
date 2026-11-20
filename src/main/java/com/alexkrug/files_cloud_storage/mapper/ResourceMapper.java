package com.alexkrug.files_cloud_storage.mapper;
import com.alexkrug.files_cloud_storage.dto.DirectoryDto;
import com.alexkrug.files_cloud_storage.dto.FileDto;
import com.alexkrug.files_cloud_storage.entity.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(source = "pathWithoutPrefix", target = "path")
    DirectoryDto toDirectoryDto(Resource resource, String pathWithoutPrefix);

    @Mapping(source = "pathWithoutPrefix", target = "path")
    FileDto toFileDto(Resource resource, String pathWithoutPrefix);
}
