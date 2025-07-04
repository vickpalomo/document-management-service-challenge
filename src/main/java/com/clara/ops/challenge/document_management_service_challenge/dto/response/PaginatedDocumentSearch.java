package com.clara.ops.challenge.document_management_service_challenge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO para encapsular resultados de búsqueda paginada de documentos.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedDocumentSearch {
    private Metadata metadata;
    private List<DocumentResponse> documents;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Metadata {
        private int currentPage;
        private int itemsPerPage;
        private int currentItems;
        private int totalPages;
        private long totalItems;
    }
}
