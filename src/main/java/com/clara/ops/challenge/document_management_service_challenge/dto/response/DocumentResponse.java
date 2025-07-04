package com.clara.ops.challenge.document_management_service_challenge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
    private UUID id;
    private String userName;
    private String documentName;
    private List<String> tags;
    private long fileSize;
    private String fileType;
    private Instant createdAt;
}

