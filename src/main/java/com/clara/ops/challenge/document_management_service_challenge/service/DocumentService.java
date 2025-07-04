package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.domain.Document;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

  Document uploadDocument(
      String userName, String documentName, MultipartFile file, List<String> tags);

  Page<Document> searchDocuments(
          String userName,
          String documentName,
          List<String> tags,
          Pageable pageable
  );

  List<Document> listDocuments(String userName);

  String downloadDocument(UUID documentId);
}
