package com.gustavotbett.gitlab_mr_review.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record MergeRequestChanges(
    Integer id,
    Integer iid,
    String title,
    String description,
    String state,
    @JsonProperty("source_branch") String sourceBranch,
    @JsonProperty("target_branch") String targetBranch,
    @JsonProperty("web_url") String webUrl,
    List<Change> changes
) {
    public record Change(
        @JsonProperty("old_path") String oldPath,
        @JsonProperty("new_path") String newPath,
        @JsonProperty("a_mode") String aMode,
        @JsonProperty("b_mode") String bMode,
        @JsonProperty("new_file") Boolean newFile,
        @JsonProperty("renamed_file") Boolean renamedFile,
        @JsonProperty("deleted_file") Boolean deletedFile,
        String diff
    ) {}

    public boolean shouldIgnoreFile(String path) {
        if (path == null) return true;

        List<String> ignoredPatterns = List.of(
            "package-lock.json",
            "yarn.lock",
            "pom.xml",
            ".lock",
            "dist/",
            "target/",
            "build/",
            "node_modules/",
            ".min.js",
            ".min.css",
            ".map",
            ".svg",
            ".png",
            ".jpg",
            ".jpeg",
            ".gif",
            ".ico",
            ".woff",
            ".woff2",
            ".ttf",
            ".eot"
        );

        String lowerPath = path.toLowerCase();
        return ignoredPatterns.stream().anyMatch(lowerPath::contains);
    }

    public String getFilteredDiff() {
        if (changes == null || changes.isEmpty()) {
            return "";
        }

        StringBuilder diffBuilder = new StringBuilder();
        for (Change change : changes) {
            String path = change.newPath() != null ? change.newPath() : change.oldPath();

            if (shouldIgnoreFile(path)) {
                continue;
            }

            if (change.diff() != null && !change.diff().isBlank()) {
                diffBuilder.append("### File: ").append(path).append("\n");
                if (Boolean.TRUE.equals(change.newFile())) {
                    diffBuilder.append("(New file)\n");
                } else if (Boolean.TRUE.equals(change.deletedFile())) {
                    diffBuilder.append("(Deleted file)\n");
                } else if (Boolean.TRUE.equals(change.renamedFile())) {
                    diffBuilder.append("(Renamed from: ").append(change.oldPath()).append(")\n");
                }
                diffBuilder.append("```diff\n");
                diffBuilder.append(change.diff());
                diffBuilder.append("\n```\n\n");
            }
        }
        return diffBuilder.toString();
    }

    public int getTotalDiffLines() {
        if (changes == null) return 0;

        return changes.stream()
            .filter(c -> !shouldIgnoreFile(c.newPath() != null ? c.newPath() : c.oldPath()))
            .mapToInt(c -> c.diff() != null ? c.diff().split("\n").length : 0)
            .sum();
    }
}
