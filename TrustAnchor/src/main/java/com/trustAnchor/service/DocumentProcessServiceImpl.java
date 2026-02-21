package com.trustAnchor.service;

import com.trustAnchor.model.DocumentChunk;
import com.trustAnchor.model.DocumentMetadata;
import com.trustAnchor.repository.DocumentChunkRepository;
import com.trustAnchor.repository.DocumentMetadataRepository;
import com.trustAnchor.util.IngestionStatus;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentProcessServiceImpl implements DocumentProcessService{

    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingModel embeddingModel;
    private final DocumentMetadataRepository metadataRepository;

    @Override
    public void processDocument(MultipartFile file, DocumentMetadata metadata) {
        Thread.ofVirtual().start(() -> {
            try(InputStream inputStream = file.getInputStream()) {

                // Parsing the multipart file into document for spliting and embeding
                DocumentParser parser = new ApachePdfBoxDocumentParser();
                Document document = parser.parse(inputStream);


                // Creating a Splitter (500 chars with 50 char overlap)
                DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);

                // Splitiing the document into TextSegments
                List<TextSegment> segmentList = splitter.split(document);

                // For each segment, create a vector and saving it to postgres
                for(TextSegment segment: segmentList){
                    // Generating the embedding (float array)
                    float[] array = embeddingModel.embed(segment.text()).content().vector();

                    // Creating DocumentChunk Entity
                    DocumentChunk documentChunk = new DocumentChunk();
                    documentChunk.setDocument(metadata);
                    documentChunk.setContent(segment.text());
                    documentChunk.setEmbedding(array);

                    // Saving to postgres
                    chunkRepository.save(documentChunk);
                }
                // 3. Embed & Save
                metadata.setStatus(IngestionStatus.COMPLETED);
                metadataRepository.save(metadata);
            } catch (Exception e) {
                metadata.setStatus(IngestionStatus.FAILED);
                metadataRepository.save(metadata);
            }
        });

    }
}
