package com.gustavotbett.gitlab_mr_review;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"spring.ai.openai.api-key=test-key-for-testing",
	"gitlab.token=test-token",
	"gitlab.url=https://gitlab.com"
})
class MrReviewApplicationTests {

	@Test
	void contextLoads() {
	}

}
