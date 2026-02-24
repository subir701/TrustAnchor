package com.trustAnchor.util;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryResponseDTO {

    private String message;
    private LocalDateTime createdAt;
}
