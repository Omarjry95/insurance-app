package com.cms.chatbot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Configuration
@Slf4j
public class OpenAIRestTemplateConfig {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer";

  @Value("${openai.api.key}")
  private String openAIApiKey;

  @Bean
  public RestTemplate openAIRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();

    restTemplate.getInterceptors()
        .add((request, body, execution) -> {
          HashMap<String, List<String>> headers = new HashMap<>();

          headers.put(AUTHORIZATION_HEADER, Collections.singletonList(String.join(" ", BEARER_PREFIX, openAIApiKey)));

          request.getHeaders()
              .addAll(new MultiValueMapAdapter<>(headers));

          log.info("Request headers: {}", request.getHeaders());
          log.info("Request body: {}", new String(body, StandardCharsets.UTF_8));

          return execution.execute(request, body);
        });

    return restTemplate;
  }
}
