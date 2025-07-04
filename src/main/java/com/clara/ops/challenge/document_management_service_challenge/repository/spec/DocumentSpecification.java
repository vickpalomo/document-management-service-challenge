package com.clara.ops.challenge.document_management_service_challenge.repository.spec;

import com.clara.ops.challenge.document_management_service_challenge.domain.Document;
import com.clara.ops.challenge.document_management_service_challenge.domain.Tag;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;


import java.util.List;

public class DocumentSpecification {

    public static Specification<Document> byUserName(String userName) {
        return (root, query, cb) -> {
            if (userName == null || userName.isBlank()) {
                return null;
            }
            return cb.equal(root.get("userName"), userName);
        };
    }

    public static Specification<Document> byDocumentName(String documentName) {
        return (root, query, cb) -> {
            if (documentName == null || documentName.isBlank()) {
                return null;
            }
            return cb.like(
                    cb.lower(root.get("documentName")),
                    "%" + documentName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Document> byTags(List<String> tagNames) {
        return (root, query, cb) -> {
            if (tagNames == null || tagNames.isEmpty()) {
                return null;
            }
            Join<Document, Tag> tags = root.join("tags");
            return tags.get("name").in(tagNames);
        };
    }
}
