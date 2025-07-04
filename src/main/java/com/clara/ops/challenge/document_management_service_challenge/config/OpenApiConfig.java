package com.clara.ops.challenge.document_management_service_challenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Management Service API")
                        .version("v0.0.1")
                        .description("API para subir, buscar y descargar documentos PDF")
                        .contact(new Contact().name("Victor Palomo").email("ing.victorpalomo@gmail.com")));
    }

    @Bean
    public GroupedOpenApi documentApi() {
        return GroupedOpenApi.builder()
                .group("documents")
                .pathsToMatch("/document-management/**")
                .build();
    }
}

