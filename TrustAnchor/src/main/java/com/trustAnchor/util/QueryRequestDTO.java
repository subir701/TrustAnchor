package com.trustAnchor.util;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequestDTO {

    private String message;
    private LocalDateTime createdAt;

}
