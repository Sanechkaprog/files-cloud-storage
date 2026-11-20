package com.alexkrug.files_cloud_storage.repository;


import com.alexkrug.files_cloud_storage.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {

    Optional<Resource> findResourceRecordByPathAndName(String path, String name);

    List<Resource> findResourceRecordByPath(String path);

    List<Resource> findResourceRecordByNameContainingAndPathStartingWith(String name, String prefix);

    void deleteResourceRecordByPathAndName(String path, String name);

    List<Resource> findResourceRecordByPathStartingWith(String path);


}
