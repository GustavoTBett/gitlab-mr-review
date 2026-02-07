package com.gustavotbett.gitlab_mr_review.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient gitlabRestClient(GitLabProperties gitLabProperties) {
        return RestClient.builder()
                .baseUrl(gitLabProperties.getUrl())
                .defaultHeader("PRIVATE-TOKEN", gitLabProperties.getToken())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
