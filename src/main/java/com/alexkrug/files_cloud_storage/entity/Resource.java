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
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
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

    public Resource(){}

    public Resource(String path, String name, Long size, ResourceType type) {
        this.path = path;
        this.name = name;
        this.size = size;
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
