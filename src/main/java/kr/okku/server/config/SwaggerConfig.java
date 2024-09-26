package kr.okku.server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // Define a security scheme for the Authorization header
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization") // The name of the header field
                .scheme("bearer");

        // Apply the security requirement globally
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Authorization", securityScheme)) // Register the security scheme
                .addSecurityItem(securityRequirement) // Apply globally to all endpoints
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("okku api docs")
                .description("우측 상단에서 Authorization token 설정 가능")
                .version("1.0.0");
    }
}
