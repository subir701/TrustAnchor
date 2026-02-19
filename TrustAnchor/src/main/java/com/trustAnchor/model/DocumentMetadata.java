package com.trustAnchor.model;


import com.trustAnchor.util.IngestionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String filename;

    // To prevent duplicate uploads
    @Column(unique = true, nullable = true)
    private String checkSum;

    private Long fileSize;

    // Pro-Tip: Add this to track document length
    private Integer totalPages;

    @Enumerated(EnumType.STRING)
    private IngestionStatus status = IngestionStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist(){
        if(this.status == null){
            this.status = IngestionStatus.PENDING;
        }

        if(this.createdAt == null){
            this.createdAt = LocalDateTime.now();
        }
    }

    // Helper to calculate if we are within the 2GB limit
    public static long bytesToGb(long bytes){
        return bytes / (1024 * 1024 * 1024);
    }
}
