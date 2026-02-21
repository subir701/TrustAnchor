package com.trustAnchor.service;

import com.trustAnchor.exception.StorageQuotaExceededException;
import com.trustAnchor.exception.TrustAnchorException;
import com.trustAnchor.model.DocumentMetadata;
import com.trustAnchor.repository.DocumentChunkRepository;
import com.trustAnchor.repository.DocumentMetadataRepository;
import com.trustAnchor.util.IngestionStatus;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IngestionServiceImpl implements IngestionService{


    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingModel embeddingModel;
    private final DocumentProcessService documentProcessService;
    private final DocumentMetadataRepository documentMetadataRepository;
    private final long MAX_QUOTA_BYTES = 2L * 1024 * 1024 * 1024;

    @Transactional
    @Override
    public UUID ingestFile(MultipartFile file) {

        // Calculating Checksum
        String checkSum = calculateCheckSum(file);

        // Checking for dublicates
        if(documentMetadataRepository.existsByCheckSum(checkSum)){
            throw new TrustAnchorException("File already exists.");
        }

        // Checking Qutoa
        long currentUsage = documentMetadataRepository.getTotalStorageUsed();
        long newFileSize = file.getSize();

        if(currentUsage + newFileSize > MAX_QUOTA_BYTES){
            throw new StorageQuotaExceededException("Storage quota exceeded. Max allowed is 2GB.");
        }

        // Saving Initial Metadata (PENDING status)
        DocumentMetadata metadata = DocumentMetadata.builder()
                .filename(file.getOriginalFilename())
                .fileSize(newFileSize)
                .checkSum(checkSum)
                .build();

        documentMetadataRepository.save(metadata);

        // 5. Start background processing (example)
        documentProcessService.processDocument(file, metadata);


        return metadata.getId();
    }

    @Override
    public String calculateCheckSum(MultipartFile file) {

        try(InputStream inputStream = file.getInputStream()){

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[8192];
            int bytesRead;

            while((bytesRead = inputStream.read(buffer)) != -1){
                digest.update(buffer, 0, bytesRead);
            }

            byte[] hashBytes = digest.digest();

            //convert to hex string
            StringBuilder hexString = new StringBuilder();
            for(byte b: hashBytes){
                hexString.append(String.format("%02x",b));
            }

            return hexString.toString();
        }catch(Exception ex){
            throw new RuntimeException("Failed to calculate check sum ", ex);
        }
    }
}
