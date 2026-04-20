package com.app.trashmasters.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI trashmastersOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trashmasters API")
                        .description("Smart waste-collection route optimization platform. "
                                + "Uses IoT sensors, ML predictions (SageMaker), and OR-Tools VRP solver "
                                + "to generate optimized collection routes for 4–8 yard commercial/public bins.")
                        .version("1.0.0")
                        .contact(new Contact().name("Trashmasters Team")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local dev")));
    }
}

