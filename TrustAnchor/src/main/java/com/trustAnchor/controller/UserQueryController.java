package com.trustAnchor.controller;

import com.trustAnchor.service.UserQueryService;
import com.trustAnchor.util.QueryRequestDTO;
import com.trustAnchor.util.QueryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trustanchor/querys")
@RequiredArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;

    @PostMapping("/ask")
    public ResponseEntity<QueryResponseDTO> getQueryResponse(@RequestBody QueryRequestDTO requestDTO){
        // Calling the service to perform Embedding -> Search -> LLM Generation
        QueryResponseDTO response = userQueryService.getQueryResponse(requestDTO);

        // Returning the final AI-generated answer
        return ResponseEntity.ok(response);
    }
}
