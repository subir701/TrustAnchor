package com.trustAnchor.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name = "document_chunks")
@Getter
@Setter
@NoArgsConstructor
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DocumentMetadata document;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "embedding", columnDefinition = "vector(384)")
    private float[] embedding;
}


