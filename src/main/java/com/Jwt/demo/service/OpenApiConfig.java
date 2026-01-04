package com.Jwt.demo.service;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Layman terms:
 * This sets up the Swagger/OpenAPI documentation and says our API uses
 * a Bearer token (JWT) for security.
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI api() {
		String schemeName = "bearerAuth";
		SecurityScheme bearerScheme = new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT");
		return new OpenAPI()
				.info(new Info().title("User Foundation API").version("v1"))
				.addSecurityItem(new SecurityRequirement().addList(schemeName))
				.components(new Components().addSecuritySchemes(schemeName, bearerScheme));
	}
}
