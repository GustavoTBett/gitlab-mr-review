package com.gustavotbett.gitlab_mr_review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeReviewService {

    private final ChatClient.Builder chatClientBuilder;

    private static final String REVIEW_PROMPT = """
            Você é um revisor de código experiente em boas práticas de desenvolvimento.
            
            Analise o seguinte Merge Request e identifique:
            - 🐛 Bugs potenciais
            - ⚡ Problemas de performance
            - 📖 Problemas de legibilidade
            - ⚠️ Más práticas
            - 🔒 Problemas de segurança
            - 💡 Sugestões de melhoria
            
            Regras:
            - Seja objetivo e educado
            - Escreva em português
            - Se o código estiver bom, elogie brevemente
            - Não repita o código, apenas referencie arquivo e linha quando possível
            - Formate a resposta em Markdown
            - Limite sua resposta a no máximo 2000 caracteres
            
            ## Merge Request: %s
            **Branch:** %s → %s
            
            ## Diff:
            %s
            """;

    public String reviewCode(String mrTitle, String sourceBranch, String targetBranch, String diff) {

        try {
            String prompt = String.format(REVIEW_PROMPT, mrTitle, sourceBranch, targetBranch, diff);

            ChatClient chatClient = chatClientBuilder.build();

            String review = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            return formatReviewComment(review);

        } catch (Exception e) {
            throw new RuntimeException("Failed to perform code review", e);
        }
    }

    private String formatReviewComment(String review) {
        return """
                🤖 **AI Code Review**
                
                %s
                
                ---
                _Este review foi gerado automaticamente por IA. Sempre valide as sugestões._
                """.formatted(review);
    }
}
