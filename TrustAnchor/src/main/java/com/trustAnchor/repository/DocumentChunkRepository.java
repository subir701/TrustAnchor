package com.trustAnchor.repository;

import com.trustAnchor.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, UUID> {

    /**
     * Performs a semantic similarity search using the PGVector Cosine Distance operator (<=>).
     * This method retrieves the most contextually relevant text chunks for a given embedding.
     *
     * @param queryEmbedding The vector representation of the user's question.
     * @param limit The number of top-K results to retrieve (e.g., 3-5).
     * @return A list of DocumentChunks sorted by proximity to the query.
     */
    @Query(value = """
        SELECT * 
        FROM document_chunks
        ORDER BY embedding <=> :queryEmbedding
        LIMIT :limit
        """,
            nativeQuery = true)
    List<DocumentChunk> findSimilarChunks(
            @Param("queryEmbedding") float[] queryEmbedding,
            @Param("limit") int limit
    );

    /**
     * Searches for the most relevant chunks within a specific document.
     * This is essential for scoped RAG (e.g., "Search only in this specific PDF").
     *
     * @Param queryEmbedding The vector representation for the user's question.
     * @Param documentId Doucument Id to correct document
     * @Param limit  The number of top-K result to retrieve
     * @return A list of DocumentChunks sorted by proximity to the query.
     */

    @Query(value = """
        SELECT *
        FROM document_chunks 
        WHERE document_id = :documentId
        ORDER BY embedding <=> :queryEmbedding
        LIMIT :limit""", nativeQuery = true)
    List<DocumentChunk> findSimilarChunksInDocument(float[] queryEmbedding, UUID documentId, int limit);


}
