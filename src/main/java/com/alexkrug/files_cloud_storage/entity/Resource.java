package com.alexkrug.files_cloud_storage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "resources")
public class Resource implements Serializable {
    @Id
    @Column(name = "resource_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID resourceId;

    @NotNull
    @Size(max = 1000)
    private String path;

    @NotNull
    @Size(max = 255)
    private String name;

    private Long size;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ResourceType type;

    public Resource() {}

    public Resource(String path, String name, Long size, ResourceType type) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.type = type;
    }

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

    @Override
    public String toString() {
        return "ResourceRecord{" +
                "resourceId=" + resourceId +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", type=" + type +
                '}';
    }
}
