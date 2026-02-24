package com.trustAnchor.service;

import com.trustAnchor.exception.ResponseFailedException;
import com.trustAnchor.model.DocumentChunk;
import com.trustAnchor.repository.DocumentChunkRepository;
import com.trustAnchor.util.QueryRequestDTO;
import com.trustAnchor.util.QueryResponseDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService{

    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingModel embeddingModel;
    private final ChatLanguageModel chatLanguageModel;

    @Override
    public QueryResponseDTO getQueryResponse(QueryRequestDTO requestDTO) {

        StringBuilder response = new StringBuilder();

        try{
            // Generating Vectors from the user query
            float[] vectors = embeddingModel.embed(requestDTO.getMessage()).content().vector();

            // Getting context chunks for the method
            List<String> list = getChunks(vectors);

            // No chunks is matching with user query
            if(list.isEmpty()){
                return new QueryResponseDTO("I'm sorry, I don't have any documents uploaded to answer that question", LocalDateTime.now());
            }

            StringBuilder context = new StringBuilder();

            System.out.println("Creating context");

            // Iterating on list of chunks to create a context
            for(String chunk: list){
                context.append(chunk).append("\n\n");
            }

            System.out.println("Here is the context "+context.toString());

            String prompt = "You are a helpful assistant. " +
                    "Answer the question based ONLY on the provided context. " +
                    "If the answer isn't in the context, say you don't know.\n\n" +
                    "Context:\n" +
                    context.toString() +
                    "\n\nQuestion:\n" +
                    requestDTO.getMessage();

            // Send Prompt to AI model
             response.append(chatLanguageModel.generate(prompt));


        }catch(ResponseFailedException ex){
            throw new ResponseFailedException("Failed to generate the response");
        }

        return new QueryResponseDTO(response.toString(),LocalDateTime.now());

    }

    @Override
    public List<String> getChunks(float[] vector) {
        int limit = 3;

        return chunkRepository.findSimilarContent(vector,limit);
    }

}
