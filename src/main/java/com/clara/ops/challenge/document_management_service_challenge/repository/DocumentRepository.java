package com.clara.ops.challenge.document_management_service_challenge.repository;

import com.clara.ops.challenge.document_management_service_challenge.domain.Document;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID>,
        JpaSpecificationExecutor<Document> {
  Optional<Document> findByChecksumAndUserName(String checksum, String userName);

  List<Document> findByUserNameOrderByCreatedAtDesc(String userName);

  @Override
  Page<Document> findAll(Specification<Document> spec, Pageable pageable);

}
