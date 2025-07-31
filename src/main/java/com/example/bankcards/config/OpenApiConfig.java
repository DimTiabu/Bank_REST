package com.example.bankcards.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankCardsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Cards API")
                        .description("API для управления банковскими картами")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Dmitry Tiabutov")
                                .email("d.tyabutov@yandex.ru"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Документация проекта")
                        .url("https://github.com/DimTiabu/Bank_REST"));
    }
}