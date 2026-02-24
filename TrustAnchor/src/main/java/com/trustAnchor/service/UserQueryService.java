package com.trustAnchor.service;

import com.trustAnchor.model.DocumentChunk;
import com.trustAnchor.util.QueryRequestDTO;
import com.trustAnchor.util.QueryResponseDTO;

import java.util.List;

public interface UserQueryService {

    QueryResponseDTO getQueryResponse(QueryRequestDTO requestDTO);
    List<String> getChunks(float[] vector);
}
