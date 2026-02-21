package com.trustAnchor.repository;

import com.trustAnchor.model.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadata, UUID> {

    @Query("SELECT SUM(d.fileSize) FROM DocumentMetadata d")
    Long getTotalStorageUsed();

    // Also, we need a way to check if a file with the same checksum already exists
    boolean existsByCheckSum(String checkSum);
}
