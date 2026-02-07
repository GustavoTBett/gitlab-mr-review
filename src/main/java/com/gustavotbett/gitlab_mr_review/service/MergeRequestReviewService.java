package com.gustavotbett.gitlab_mr_review.service;

import com.gustavotbett.gitlab_mr_review.controller.dto.MergeRequestChanges;
import com.gustavotbett.gitlab_mr_review.controller.dto.MergeRequestWebhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MergeRequestReviewService {

    private final GitLabService gitLabService;
    private final CodeReviewService codeReviewService;

    @Async
    public void processMergeRequest(MergeRequestWebhook webhook) {
        Integer projectId = webhook.getProjectId();
        Integer mrIid = webhook.getMergeRequestIid();

        try {
            MergeRequestChanges changes = gitLabService.getMergeRequestChanges(projectId, mrIid);

            if (changes == null || changes.changes() == null || changes.changes().isEmpty()) {
                return;
            }

            int totalLines = changes.getTotalDiffLines();
            if (!gitLabService.isDiffWithinLimit(totalLines)) {
                String skipComment = """
                        🤖 **AI Code Review**
                        
                        ⚠️ Este MR contém muitas alterações (%d linhas de diff) e excede o limite configurado de %d linhas.
                        
                        O review automático foi ignorado para evitar análises superficiais em grandes mudanças.
                        """.formatted(totalLines, gitLabService.getMaxDiffLines());

                gitLabService.postMergeRequestComment(projectId, mrIid, skipComment);
                return;
            }

            String filteredDiff = changes.getFilteredDiff();

            if (filteredDiff.isBlank()) {
                return;
            }

            String review = codeReviewService.reviewCode(
                    changes.title(),
                    changes.sourceBranch(),
                    changes.targetBranch(),
                    filteredDiff
            );

            gitLabService.postMergeRequestComment(projectId, mrIid, review);
        } catch (Exception e) {
            throw new RuntimeException("Error processing merge request: " + e.getMessage(), e);
        }
    }
}
