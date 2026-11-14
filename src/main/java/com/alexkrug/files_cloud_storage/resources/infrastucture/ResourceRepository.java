package com.alexkrug.files_cloud_storage.resources.infrastucture;

import com.alexkrug.files_cloud_storage.resources.domain.ResourceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceRecord, UUID> {

    Optional<ResourceRecord> findResourceRecordByPathAndName(String path, String name);

    List<ResourceRecord> findResourceRecordByPath(String path);
}
