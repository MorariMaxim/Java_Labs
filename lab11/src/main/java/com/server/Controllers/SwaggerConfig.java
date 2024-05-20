package com.server.Controllers;

import io.swagger.v3.oas.models.info.Info;
import springfox.documentation.builders.RequestHandlerSelectors;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Books API")
                        .version("1.0")
                        .description("API for managing books")
                        .contact(new Contact().name("Morari Maxim").email("mmaxim2291%gmail.com")));
    }
}
