// 예: src/main/java/kr/or/ddit/config/AppConfig.java
package kr.or.ddit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // RestTemplate 빈 등록
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // ObjectMapper 빈 등록 (필요한 경우)
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
