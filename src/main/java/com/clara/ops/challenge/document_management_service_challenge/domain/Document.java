package com.clara.ops.challenge.document_management_service_challenge.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "document", schema = "document_schema")
@EntityListeners(AuditingEntityListener.class)
public class Document {
  @Id
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "user_name", nullable = false)
  private String userName;

  @Column(name = "document_name", nullable = false)
  private String documentName;

  @Column(name = "original_filename", nullable = false)
  private String originalFilename;

  @Column(columnDefinition = "text")
  private String description;

  @Column(name = "minio_path", nullable = false)
  private String minioPath;

  @Column(name = "file_size", nullable = false)
  private Long fileSize;

  @Column(name = "file_type", nullable = false)
  private String fileType;

  @Column(length = 64)
  private String checksum;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @ManyToMany
  @JoinTable(
      name = "document_tag",
      schema = "document_schema",
      joinColumns = @JoinColumn(name = "document_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags;
}
