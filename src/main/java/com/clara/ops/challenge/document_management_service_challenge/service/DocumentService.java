package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.domain.Document;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

  Document uploadDocument(
      String userName, String documentName, MultipartFile file, List<String> tags);
}
