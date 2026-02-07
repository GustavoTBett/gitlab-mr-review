package com.gustavotbett.gitlab_mr_review.controller;

import com.gustavotbett.gitlab_mr_review.controller.dto.MergeRequestWebhook;
import com.gustavotbett.gitlab_mr_review.service.GitLabService;
import com.gustavotbett.gitlab_mr_review.service.MergeRequestReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("gitlab")
@RequiredArgsConstructor
public class MrReviewController {

    private final GitLabService gitLabService;
    private final MergeRequestReviewService mergeRequestReviewService;

    @PostMapping("/webhook")
    public ResponseEntity<String> gitlabWebhook(
            @RequestHeader(value = "X-Gitlab-Token", required = true) String gitlabToken,
            @RequestBody MergeRequestWebhook webhook) {

        if (!gitLabService.validateWebhookToken(gitlabToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        if (!webhook.isMergeRequestEvent()) {
            return ResponseEntity.ok("Event ignored - not a merge request");
        }

        if (!webhook.isValidAction()) {
            return ResponseEntity.ok("Action ignored");
        }

        if (webhook.isDraft()) {
            return ResponseEntity.ok("Draft MR ignored");
        }

        log.info("Processing MR webhook: project={}, MR=!{}, action={}",
                webhook.getProjectId(),
                webhook.getMergeRequestIid(),
                webhook.objectAttributes().action());

        mergeRequestReviewService.processMergeRequest(webhook);

        return ResponseEntity.ok("MR review started");
    }
}
