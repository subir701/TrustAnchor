package com.trustAnchor.service;

import com.trustAnchor.exception.ResponseFailedException;
import com.trustAnchor.repository.DocumentChunkRepository;
import com.trustAnchor.util.QueryRequestDTO;
import com.trustAnchor.util.QueryResponseDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService{

    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingModel embeddingModel;
    private final ChatLanguageModel chatLanguageModel;
    private final RedisCacheService redisCacheService;

    @Override
    public QueryResponseDTO getQueryResponse(QueryRequestDTO requestDTO) {
        String query = requestDTO.getMessage();

        // 1. L1 Cache Check (Exact Match)
        String l1Cache = redisCacheService.checkL1Cache(query);
        // Use Objects.nonNull or check for null to avoid NPE
        if (l1Cache != null && !l1Cache.isEmpty()) {
            return new QueryResponseDTO(l1Cache, LocalDateTime.now());
        }

        // 2. L2 Cache Check (Semantic Match)
        String l2Cache = redisCacheService.checkL2Cache(query);
        if (l2Cache != null) {
            // Optimization: If found in L2, save to L1 for faster future lookups
            redisCacheService.createPair(query, l2Cache);
            return new QueryResponseDTO(l2Cache, LocalDateTime.now());
        }

        try {
            // Generating Vectors from the user query
            float[] vectors = embeddingModel.embed(query).content().vector();

            // Getting context chunks
            List<String> list = getChunks(vectors);

            if (list.isEmpty()) {
                return new QueryResponseDTO("I'm sorry, I don't have enough context to answer that.", LocalDateTime.now());
            }

            StringBuilder context = new StringBuilder();
            for (String chunk : list) {
                context.append(chunk).append("\n\n");
            }

            System.out.println(context.toString());

            String prompt = "You are a professional assistant. Answer the question using ONLY the provided text segments. "
                    + "If the answer is not in the segments, say 'I do not have information on that in the current document.'\n\n"
                    + "Context: "+ context
                    + "Question: "+ query;

            System.out.println(prompt);

            // 3. Send Prompt to AI model
            String aiResponse = chatLanguageModel.generate(prompt);

            // --- CRITICAL: UPDATE CACHES ---
            // Save the result so the NEXT query is fast!
            redisCacheService.createPair(query, aiResponse); // Update L1
            redisCacheService.saveToL2Cache(query, aiResponse); // Update L2

            return new QueryResponseDTO(aiResponse, LocalDateTime.now());

        } catch (Exception ex) {
            // Use a generic Exception catch or specific ones to avoid crashing
            throw new ResponseFailedException("Failed to generate response: " + ex.getMessage());
        }
    }

    @Override
    public List<String> getChunks(float[] vector) {
        int limit = 5;

        return chunkRepository.findSimilarContent(vector,limit);
    }

}
