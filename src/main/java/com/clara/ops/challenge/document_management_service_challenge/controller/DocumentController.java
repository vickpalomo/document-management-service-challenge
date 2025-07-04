package com.clara.ops.challenge.document_management_service_challenge.controller;

import com.clara.ops.challenge.document_management_service_challenge.domain.Document;
import com.clara.ops.challenge.document_management_service_challenge.domain.Tag;
import com.clara.ops.challenge.document_management_service_challenge.dto.exception.ErrorResponse;
import com.clara.ops.challenge.document_management_service_challenge.dto.request.UploadDocumentRequest;
import com.clara.ops.challenge.document_management_service_challenge.dto.response.DocumentDownloadDto;
import com.clara.ops.challenge.document_management_service_challenge.dto.response.DocumentResponse;
import com.clara.ops.challenge.document_management_service_challenge.dto.response.PaginatedDocumentSearch;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/document-management")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Document Management", description = "Endpoints para gestión de documentos PDF")
@Validated
public class DocumentController {
  private final DocumentService documentService;

  public DocumentController(DocumentService documentService) {
    this.documentService = documentService;
  }

    @Operation(
            summary = "Upload a PDF document",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = UploadDocumentRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Document uploaded successfully",
                            content = @Content(schema = @Schema(implementation = Document.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "409", description = "Conflict",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
  @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Document> uploadDocument(@Valid @ModelAttribute UploadDocumentRequest request) {
    Document doc = documentService.uploadDocument(
            request.getUserName(),
            request.getDocumentName(),
            request.getFile(),
            request.getTags()
    );
    return new ResponseEntity<>(doc, HttpStatus.CREATED);
  }

    @Operation(
            summary = "Search documents with optional filters",
            parameters = {
                    @Parameter(name = "userName", in = ParameterIn.QUERY, description = "Filter by user", schema = @Schema(type = "string")),
                    @Parameter(name = "documentName", in = ParameterIn.QUERY, description = "Filter by document name", schema = @Schema(type = "string")),
                    @Parameter(name = "tags", in = ParameterIn.QUERY, description = "Filter by tags", schema = @Schema(type = "array", implementation = String.class)),
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Zero-based page index", schema = @Schema(type = "integer", defaultValue = "0")),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer", defaultValue = "20")),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Sort criteria property,asc|desc", schema = @Schema(type = "string", defaultValue = "createdAt,desc"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedDocumentSearch.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/search")
    public ResponseEntity<PaginatedDocumentSearch> searchDocuments(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String documentName,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        String[] parts = sort.split(",");
        Sort.Direction dir = parts.length > 1 && parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort s = Sort.by(dir, parts[0]);
        Pageable pageable = PageRequest.of(page, size, s);

        Page<Document> resultPage = documentService.searchDocuments(userName, documentName, tags, pageable);

        List<DocumentResponse> items = resultPage.getContent().stream()
                .map(doc -> new DocumentResponse(
                        doc.getId(),
                        doc.getUserName(),
                        doc.getDocumentName(),
                        doc.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                        doc.getFileSize(),
                        doc.getFileType(),
                        doc.getCreatedAt()
                ))
                .collect(Collectors.toList());

        PaginatedDocumentSearch.Metadata meta = new PaginatedDocumentSearch.Metadata(
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getNumberOfElements(),
                resultPage.getTotalPages(),
                resultPage.getTotalElements()
        );
        PaginatedDocumentSearch dto = new PaginatedDocumentSearch(meta, items);

        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Download a document",
            parameters = {@Parameter(name = "documentId", in = ParameterIn.PATH,
                    description = "ID of the document to download", schema = @Schema(type = "string"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Download URL returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentDownloadDto.class))),
                    @ApiResponse(responseCode = "404", description = "Document not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/download/{documentId}")
    public ResponseEntity<DocumentDownloadDto> downloadDocument(@PathVariable UUID documentId) {
        String url = documentService.downloadDocument(documentId);
        DocumentDownloadDto dto = new DocumentDownloadDto(url);
        return ResponseEntity.ok(dto);
    }
}
