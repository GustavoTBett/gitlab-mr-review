package com.gustavotbett.gitlab_mr_review.service;

import com.gustavotbett.gitlab_mr_review.config.GitLabProperties;
import com.gustavotbett.gitlab_mr_review.controller.dto.MergeRequestChanges;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitLabService {

    private final RestClient gitlabRestClient;
    private final GitLabProperties gitLabProperties;

    /**
     * Busca as alterações (diff) de um Merge Request
     * GET /api/v4/projects/{projectId}/merge_requests/{iid}/changes
     */
    public MergeRequestChanges getMergeRequestChanges(Integer projectId, Integer mergeRequestIid) {

        try {
            MergeRequestChanges changes = gitlabRestClient.get()
                    .uri("/api/v4/projects/{projectId}/merge_requests/{iid}/changes", projectId, mergeRequestIid)
                    .retrieve()
                    .body(MergeRequestChanges.class);

            return changes;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch MR changes", e);
        }
    }

    /**
     * Posta um comentário no Merge Request
     * POST /api/v4/projects/{projectId}/merge_requests/{iid}/notes
     */
    public void postMergeRequestComment(Integer projectId, Integer mergeRequestIid, String comment) {

        try {
            gitlabRestClient.post()
                    .uri("/api/v4/projects/{projectId}/merge_requests/{iid}/notes", projectId, mergeRequestIid)
                    .body(Map.of("body", comment))
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception e) {
            throw new RuntimeException("Failed to post MR comment", e);
        }
    }

    /**
     * Valida o token do webhook
     */
    public boolean validateWebhookToken(String token) {
        if (gitLabProperties.getWebhookSecret() == null || gitLabProperties.getWebhookSecret().isBlank()) {
            return true;
        }
        return gitLabProperties.getWebhookSecret().equals(token);
    }

    /**
     * Verifica se o diff não excede o limite máximo de linhas
     */
    public boolean isDiffWithinLimit(int totalLines) {
        return totalLines <= gitLabProperties.getMaxDiffLines();
    }

    public int getMaxDiffLines() {
        return gitLabProperties.getMaxDiffLines();
    }
}
