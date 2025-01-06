package atu.ie.libraryadvisor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AdvisorConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
