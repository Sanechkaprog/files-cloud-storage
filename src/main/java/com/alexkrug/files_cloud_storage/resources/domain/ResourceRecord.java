package com.alexkrug.files_cloud_storage.resources.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "resources")
public class ResourceRecord {
    @Id
    @Column(name = "resource_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID resourceId;

    @NotNull
    @Max(1000)
    private String path;

    @NotNull
    @Max(255)
    private String name;

    private Long size;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ResourceType type;

    public UUID getResourceId() {
        return resourceId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }
}
