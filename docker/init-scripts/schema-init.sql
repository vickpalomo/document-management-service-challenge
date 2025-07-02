--The script to initialize the schema was sourced from the Spring Batch Core dependency: org.springframework.batch.core.

CREATE SCHEMA document_schema;
SET SCHEMA 'document_schema';

CREATE SCHEMA IF NOT EXISTS document_schema;
SET search_path TO document_schema;

-- Habilitar extensión UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tablas para Document Management Service
CREATE TABLE IF NOT EXISTS document (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_name VARCHAR(100) NOT NULL,
    document_name VARCHAR(200) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    description TEXT,
    minio_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    checksum VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tag (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS document_tag (
    document_id UUID NOT NULL REFERENCES document(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tag(id) ON DELETE CASCADE,
    PRIMARY KEY (document_id, tag_id)
);

-- Indices para mejorar rendimiento de búsqueda
CREATE INDEX IF NOT EXISTS idx_document_created_at ON document(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_tag_name ON tag(name);
CREATE INDEX IF NOT EXISTS idx_document_user_name ON document(user_name);
CREATE INDEX IF NOT EXISTS idx_document_document_name ON document(document_name);
CREATE INDEX IF NOT EXISTS idx_document_tag_tag_id ON document_tag(tag_id);