package com.gustavotbett.gitlab_mr_review.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "gitlab")
public class GitLabProperties {

    private String url;
    private String token;
    private String webhookSecret;
    private int maxDiffLines = 500;
}
