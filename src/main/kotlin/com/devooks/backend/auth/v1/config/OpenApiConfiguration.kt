package com.devooks.backend.auth.v1.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {

    companion object {
        private const val SECURITY_SCHEME_NAME = "bearerAuth"
    }

    @Bean
    fun customizeOpenApi(): OpenAPI =
        OpenAPI()
            .addSecurityItem(
                SecurityRequirement().addList(SECURITY_SCHEME_NAME)
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        SECURITY_SCHEME_NAME,
                        SecurityScheme()
                            .name(SECURITY_SCHEME_NAME)
                            .type(HTTP)
                            .scheme("Bearer")
                            .bearerFormat("JWT")
                    )
            )

}