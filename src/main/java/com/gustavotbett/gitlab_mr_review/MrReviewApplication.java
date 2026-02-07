package com.gustavotbett.gitlab_mr_review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import com.gustavotbett.gitlab_mr_review.config.GitLabProperties;

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(GitLabProperties.class)
public class MrReviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(MrReviewApplication.class, args);
	}

}
