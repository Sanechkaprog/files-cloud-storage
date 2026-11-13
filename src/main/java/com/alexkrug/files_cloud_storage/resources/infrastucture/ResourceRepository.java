package com.alexkrug.files_cloud_storage.resources.infrastucture;

import com.alexkrug.files_cloud_storage.resources.domain.ResourceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceRecord, UUID> {

    Optional<ResourceRecord> findResourceRecordByPathAfterAndName(String path, String name);
}
