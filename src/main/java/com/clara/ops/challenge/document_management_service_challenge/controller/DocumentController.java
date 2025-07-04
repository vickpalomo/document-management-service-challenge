package com.clara.ops.challenge.document_management_service_challenge.controller;

import com.clara.ops.challenge.document_management_service_challenge.domain.Document;
import com.clara.ops.challenge.document_management_service_challenge.dto.exception.ErrorResponse;
import com.clara.ops.challenge.document_management_service_challenge.dto.request.UploadDocumentRequest;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/document-management")
@Tag(name = "Document Management", description = "Endpoints para gestión de documentos PDF")
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
}
