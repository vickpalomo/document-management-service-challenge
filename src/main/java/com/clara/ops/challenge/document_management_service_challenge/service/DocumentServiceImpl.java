package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.domain.Document;
import com.clara.ops.challenge.document_management_service_challenge.domain.Tag;
import com.clara.ops.challenge.document_management_service_challenge.exception.DuplicateDocumentException;
import com.clara.ops.challenge.document_management_service_challenge.repository.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.repository.TagRepository;
import com.clara.ops.challenge.document_management_service_challenge.repository.spec.DocumentSpecification;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.InputStream;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
  private final DocumentRepository documentRepository;
  private final TagRepository tagRepository;
  private final MinioClient minioClient;

  @Value("${minio.bucket}")
  private String bucket;

  @Override
  @Transactional
  public Document uploadDocument(
      String userName, String documentName, MultipartFile file, List<String> tags) {
    String checksum = calculateChecksum(file);

    // Comprobar si ya existe un documento idéntico para este usuario
    documentRepository.findByChecksumAndUserName(checksum, userName)
            .ifPresent(existing -> {
              throw new DuplicateDocumentException(
                      "The document has already been previously uploaded with ID: " + existing.getId()
              );
            });

    // Create Document entity
    Document doc =
        Document.builder()
            .id(UUID.randomUUID())
            .userName(userName)
            .documentName(documentName)
            .originalFilename(file.getOriginalFilename())
            .fileSize(file.getSize())
            .fileType(file.getContentType())
            .checksum(calculateChecksum(file))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    Set<String> uniqueNames = new HashSet<>(tags);

    // 2. Buscar todos los Tag que ya existen de una sola vez
    List<Tag> existing = tagRepository.findAllByNameIn(uniqueNames);
    Set<String> existingNames = existing.stream()
            .map(Tag::getName)
            .collect(Collectors.toSet());

    // 3. Crear entidades para los que faltan
    List<Tag> toCreate = uniqueNames.stream()
            .filter(name -> !existingNames.contains(name))
            .map(name -> Tag.builder()
                    .id(UUID.randomUUID())
                    .name(name)
                    .build())
            .collect(Collectors.toList());

    // 4. Guardar en lote
    List<Tag> created = tagRepository.saveAll(toCreate);

    // 5. Unir ambas listas en un Set y asignar
    Set<Tag> finalTags = Stream.concat(existing.stream(), created.stream())
            .collect(Collectors.toSet());
    doc.setTags(finalTags);

    // Upload file to MinIO
    String objectName = userName + "/" + documentName + "_" + doc.getId() + ".pdf";
    try (InputStream is = file.getInputStream()) {
      minioClient.putObject(
          PutObjectArgs.builder().bucket(bucket).object(objectName).stream(is, file.getSize(), -1)
              .contentType(file.getContentType())
              .build());
    } catch (Exception e) {
      throw new RuntimeException("Error uploading file to MinIO", e);
    }

    // Update path and return
    doc.setMinioPath(objectName);
    return documentRepository.save(doc);
  }

  @Override
  public Page searchDocuments(
          String userName,
          String documentName,
          List<String> tags,
          Pageable pageable) {

    Specification<Document> spec = Specification
            .where(DocumentSpecification.byUserName(userName))
            .and(DocumentSpecification.byDocumentName(documentName))
            .and(DocumentSpecification.byTags(tags));

    return documentRepository.findAll(spec,
            PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdAt").descending()
            )
    );
  }

  @Override
  public List<Document> listDocuments(String userName) {
    return List.of();
  }

  @Override
  public String downloadDocument(UUID documentId) {
    return "";
  }

  private String calculateChecksum(MultipartFile file) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      try (InputStream is = file.getInputStream()) {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = is.read(buffer)) != -1) {
          md.update(buffer, 0, read);
        }
      }
      StringBuilder sb = new StringBuilder();
      for (byte b : md.digest()) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (Exception e) {
      throw new RuntimeException("Could not calculate checksum", e);
    }
  }
}
