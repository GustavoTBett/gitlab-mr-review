# MR Review Bot 🤖

Bot automatizado para review de código em Merge Requests do GitLab usando inteligência artificial (OpenAI).

## 📋 Funcionalidades

- ✅ Recebe webhooks do GitLab quando um MR é criado ou atualizado
- ✅ Busca o diff do Merge Request via API do GitLab
- ✅ Envia o diff para a OpenAI para análise
- ✅ Posta o review como comentário no MR
- ✅ Ignora MRs em Draft
- ✅ Ignora arquivos não relevantes (node_modules, dist, etc.)
- ✅ Limita análise a MRs com até X linhas de diff

## 🏗️ Arquitetura

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   GitLab MR     │────▶│   MR Review Bot │────▶│    OpenAI API   │
│   (Webhook)     │     │   (Spring Boot) │     │   (GPT-4o-mini) │
└─────────────────┘     └────────┬────────┘     └─────────────────┘
                                 │
                                 ▼
                        ┌─────────────────┐
                        │  GitLab API     │
                        │  (Comentário)   │
                        └─────────────────┘
```

## 🚀 Configuração

### 1. Variáveis de Ambiente

Copie o arquivo `.env.example` para `.env` e configure:

```bash
cp .env.example .env
```

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `GITLAB_URL` | URL do seu GitLab | `https://gitlab.seudominio.com` |
| `GITLAB_TOKEN` | Token de acesso do GitLab | `glpat-xxxx` |
| `GITLAB_WEBHOOK_SECRET` | Secret do webhook | `meu-secret` |
| `GITLAB_MAX_DIFF_LINES` | Limite de linhas do diff | `500` |
| `OPENAI_API_KEY` | Chave da API OpenAI | `sk-xxxx` |
| `OPENAI_MODEL` | Modelo a ser usado | `gpt-4o-mini` |

### 2. Criar Token no GitLab

1. Acesse: **User Settings > Access Tokens**
2. Crie um token com as permissões:
   - `api` (para comentar nos MRs)
   - `read_repository` (para ler o diff)

### 3. Configurar Webhook no GitLab

1. Acesse: **Seu Projeto > Settings > Webhooks**
2. Configure:
   - **URL:** `https://seu-dominio.com/gitlab/webhook`
   - **Secret Token:** (mesmo valor de `GITLAB_WEBHOOK_SECRET`)
   - **Trigger:** ✅ Merge request events
   - **SSL Verification:** ✅ Enable

## 🐳 Deploy com Docker

### Build e Run

```bash
# Build
docker build -t gitlab-mr-review:latest .

# Run
docker run -d \
  --name gitlab-mr-review \
  -p 8080:8080 \
  -e GITLAB_URL=https://gitlab.seudominio.com \
  -e GITLAB_TOKEN=glpat-xxxx \
  -e GITLAB_WEBHOOK_SECRET=meu-secret \
  -e OPENAI_API_KEY=sk-xxxx \
  gitlab-mr-review:latest
```

### Docker Compose

```bash
docker-compose up -d
```

## 🔧 Desenvolvimento Local

### Pré-requisitos

- Java 21
- Maven 3.9+

### Executar

```bash
# Compilar
./mvnw clean compile

# Executar
./mvnw spring-boot:run
```

### Testar Webhook

```bash
curl -X POST http://localhost:8080/gitlab/webhook \
  -H "Content-Type: application/json" \
  -H "X-Gitlab-Token: seu-secret" \
  -d '{
    "object_kind": "merge_request",
    "event_type": "merge_request",
    "project": {"id": 123},
    "object_attributes": {
      "iid": 1,
      "action": "open",
      "draft": false
    }
  }'
```

## 📁 Estrutura do Projeto

```
src/main/java/com/gustavotbett/mr_review/
├── MrReviewApplication.java          # Classe principal
├── config/
│   ├── GitLabProperties.java         # Propriedades de configuração
│   └── RestClientConfig.java         # Cliente HTTP para GitLab
├── controller/
│   ├── MrReviewController.java       # Endpoint do webhook
│   └── dto/
│       ├── MergeRequestWebhook.java  # DTO do webhook
│       └── MergeRequestChanges.java  # DTO das alterações do MR
└── service/
    ├── GitLabService.java            # Integração com GitLab API
    ├── CodeReviewService.java        # Integração com OpenAI
    └── MergeRequestReviewService.java # Orquestra o fluxo
```

## 📝 Fluxo de Execução

1. **Webhook recebido** → GitLab envia evento de MR
2. **Validação** → Verifica token, tipo de evento, ação e draft
3. **Busca diff** → GET `/api/v4/projects/{id}/merge_requests/{iid}/changes`
4. **Filtra arquivos** → Ignora arquivos irrelevantes
5. **Verifica limite** → Se diff > max linhas, ignora
6. **Envia para IA** → OpenAI analisa o código
7. **Posta comentário** → POST `/api/v4/projects/{id}/merge_requests/{iid}/notes`

## ⚠️ Regras de Negócio

- ✅ Ignora MRs em Draft
- ✅ Ignora eventos que não são `merge_request`
- ✅ Ignora ações que não são `open`, `update` ou `reopen`
- ✅ Ignora MRs com mais de X linhas de diff (configurável)
- ✅ Ignora arquivos: `package-lock.json`, `dist/`, `target/`, `node_modules/`, etc.

## 🔒 Segurança

- Token do webhook validado em todas as requisições
- Token do GitLab nunca exposto nos logs
- Container roda com usuário não-root

## 📄 Licença

MIT
