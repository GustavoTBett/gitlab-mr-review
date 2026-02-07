package com.gustavotbett.gitlab_mr_review.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MergeRequestWebhook(
    @JsonProperty("object_kind") String objectKind,
    @JsonProperty("event_type") String eventType,
    User user,
    Project project,
    @JsonProperty("object_attributes") ObjectAttributes objectAttributes
) {
    public record User(
        Integer id,
        String name,
        String username,
        @JsonProperty("avatar_url") String avatarUrl,
        String email
    ) {}

    public record Project(
        Integer id,
        String name,
        String description,
        @JsonProperty("web_url") String webUrl,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("git_ssh_url") String gitSshUrl,
        @JsonProperty("git_http_url") String gitHttpUrl,
        String namespace,
        @JsonProperty("visibility_level") Integer visibilityLevel,
        @JsonProperty("path_with_namespace") String pathWithNamespace,
        @JsonProperty("default_branch") String defaultBranch,
        String homepage,
        String url,
        @JsonProperty("ssh_url") String sshUrl,
        @JsonProperty("http_url") String httpUrl
    ) {}

    public record ObjectAttributes(
        Integer id,
        Integer iid,
        String title,
        @JsonProperty("source_branch") String sourceBranch,
        @JsonProperty("target_branch") String targetBranch,
        String state,
        @JsonProperty("merge_status") String mergeStatus,
        String url,
        String action,
        @JsonProperty("work_in_progress") Boolean workInProgress,
        Boolean draft,
        String description,
        @JsonProperty("source_project_id") Integer sourceProjectId,
        @JsonProperty("target_project_id") Integer targetProjectId,
        @JsonProperty("author_id") Integer authorId
    ) {}

    public boolean isMergeRequestEvent() {
        return "merge_request".equals(objectKind);
    }

    public boolean isValidAction() {
        return objectAttributes != null &&
               ("open".equals(objectAttributes.action()) ||
                "update".equals(objectAttributes.action()) ||
                "reopen".equals(objectAttributes.action()));
    }

    public boolean isDraft() {
        return objectAttributes != null &&
               (Boolean.TRUE.equals(objectAttributes.draft()) ||
                Boolean.TRUE.equals(objectAttributes.workInProgress()));
    }

    public Integer getProjectId() {
        return project != null ? project.id() : null;
    }

    public Integer getMergeRequestIid() {
        return objectAttributes != null ? objectAttributes.iid() : null;
    }
}
