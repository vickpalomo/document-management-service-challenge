package com.clara.ops.challenge.document_management_service_challenge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class UploadDocumentRequest {
  @NotBlank
  @Schema(description = "Usuario propietario del documento", example = "user1")
  private String userName;

  @NotBlank
  @Schema(description = "Nombre del documento", example = "informe.pdf")
  private String documentName;

  @NotNull
  private MultipartFile file;

  @Size(max = 10)
  @Schema(description = "Lista de etiquetas asociadas", example = "[\"finanzas\",\"2025\"]")
  private List<@NotBlank String> tags;
}
