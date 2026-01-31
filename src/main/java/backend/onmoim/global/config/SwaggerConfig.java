package backend.onmoim.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.base-url:http://localhost:8080}")
    private String baseUrl;

    @Bean
    public OpenAPI swagger() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project")
                        .description("Project Swagger")
                        .version("0.0.1"))
                .addServersItem(new Server().url("/"));
    }
}

