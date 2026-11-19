# Rede Raízes do Nordeste - API Back-End

Projeto desenvolvido para a disciplina de Projeto Multidisciplinar (Trilha Back-End) do curso de Análise e Desenvolvimento de Sistemas da UNINTER.  
Fluxo principal implementado: **Fluxo A (Pedido -> Pagamento Mock -> Atualização de Status)**.

---

## 1. Visão Geral

Esta API REST atende à operação da rede de lanchonetes **"Raízes do Nordeste"**, centralizando regras de negócio entre matriz e filiais:

- **Pedidos Multicanal:** suporte aos canais `APP`, `TOTEM`, `BALCAO`, `PICKUP` e `WEB`, com preenchimento obrigatório de `canalPedido` e filtro na listagem (`GET /pedidos?canalPedido=...`).
- **Estoque por Unidade:** cada filial possui saldo independente em `tb_estoque_unidade`, com validação e reserva atômica na criação do pedido (bloqueio com HTTP `409 Conflict` se faltar saldo).
- **Pagamento Desacoplado (Mock):** simulação de integração externa sem processar cartões localmente, tratando aprovação (avanço para cozinha) e recusa (estorno automático do estoque reservado).
- **Segurança, LGPD e Auditoria:** senhas criptografadas com `BCrypt`, autenticação via `JWT`, controle de acesso por perfil (`CLIENTE`, `ATENDENTE`, `GERENTE`, `ADMIN`), registro explícito de consentimento LGPD no cadastro e histórico de ações sensíveis em `tb_auditoria_log`.

---

## 2. Stack e Tecnologias

- **Linguagem:** Java 21 LTS
- **Framework:** Spring Boot 3.3.4 (Web, Data JPA, Security, Validation)
- **Banco de Dados:** PostgreSQL 16 (via Docker Compose)
- **Migrations:** Flyway (`V1__create_tables.sql` e `V2__seed_initial_data.sql`)
- **Autenticação:** JWT (`jjwt` 0.12.6) + `BCrypt`
- **Produtividade:** Lombok (Entidades, DTOs e injeção via construtor)
- **Documentação da API:** SpringDoc OpenAPI 3 / Swagger UI (`2.6.0`)
- **Testes:** JUnit 5, Mockito, Spring MockMvc e Postman

---

## 3. Estrutura do Projeto

```
raizes-do-nordeste-api/
├── src/main/java/br/com/uninter/raizesnordeste/
│   ├── RaizesNordesteApplication.java
│   ├── domain/               # Entidades JPA, enums e repositórios
│   │   ├── entity/           # Usuario, ConsentimentoLgpd, Unidade, Produto, EstoqueUnidade, Pedido, ItemPedido, Pagamento, AuditoriaLog
│   │   ├── enums/            # CanalPedidoEnum, StatusPedidoEnum, PerfilEnum, StatusPagamentoEnum
│   │   └── repository/       # Interfaces Spring Data JPA
│   ├── application/          # Regras de negócio e contratos de entrada/saída
│   │   ├── dto/              # DTOs de Request e Response
│   │   └── service/          # PedidoService, PagamentoService, AuthService, AuditoriaService, ProdutoService
│   ├── infrastructure/       # Segurança, tratamento de erros e config
│   │   ├── config/           # OpenApiConfig (Swagger)
│   │   ├── security/         # SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter
│   │   └── exception/        # GlobalExceptionHandler, StandardErrorDTO e exceções de domínio
│   └── api/controller/       # Controllers REST
├── src/main/resources/
│   ├── application.yml       # Configuração Spring Boot, PostgreSQL e Flyway
│   └── db/migration/
│       ├── V1__create_tables.sql      # Criação de tabelas, FKs e índices
│       └── V2__seed_initial_data.sql  # Carga inicial (filiais, produtos, estoque, usuários e LGPD)
├── src/test/java/br/com/uninter/raizesnordeste/
│   ├── service/              # PedidoServiceTest, PagamentoServiceTest, AuthServiceTest
│   └── controller/           # PedidoControllerTest (MockMvc)
├── postman/
│   └── raizes_nordeste_postman_collection.json # Coleção com 15 cenários de teste
├── docs/
│   ├── RELATORIO_TECNICO_UNINTER.md            # Relatório completo do projeto
│   └── diagramas/                              # Diagramas PNG e fontes PlantUML (.puml)
├── docker-compose.yml        # Container do PostgreSQL 16
├── .env.example              # Modelo das variáveis de ambiente
└── pom.xml                   # Dependências e build Maven
```

---

## 4. Pré-requisitos

Antes de rodar o projeto localmente, tenha instalado:
- **JDK 17** ou superior (`java -version`)
- **Maven 3.8+** (`mvn -version`)
- **Docker** e **Docker Compose** (`docker --version` e `docker compose version`)

---

## 5. Como Rodar o Projeto (Passo a Passo)

### Passo 1: Clonar o repositório e configurar variáveis de ambiente
Entre na pasta `raizes-do-nordeste-api` e crie o arquivo `.env` a partir do modelo:
```bash
cp .env.example .env
```
*(O `application.yml` já traz valores padrão compatíveis com o `docker-compose.yml` caso queira testar direto sem alterar o `.env`.)*

### Passo 2: Instalar as dependências do projeto
Baixe as dependências declaradas no `pom.xml` via Maven:
```bash
mvn dependency:resolve
```

### Passo 3: Subir o banco PostgreSQL e aplicar migrations
Inicie o container do PostgreSQL em segundo plano:
```bash
docker compose up -d
```
O banco ficará disponível em `localhost:5432` (`raizes_nordeste_db`). A criação das tabelas (`V1`) e a carga inicial de dados (`V2`) acontecem automaticamente pelo **Flyway** na subida da aplicação.

### Passo 4: Iniciar a API
```bash
mvn clean spring-boot:run
```
A API responderá em `http://localhost:8080`.

---

## 6. Documentação Swagger / OpenAPI e Links

- **Repositório Git:** `https://github.com/luizgustavo-a/raizes-do-nordeste`
- **URL do Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **Especificação OpenAPI (JSON):** `http://localhost:8080/v3/api-docs`
- **Coleção Postman:** `postman/raizes_nordeste_postman_collection.json`

Para testar rotas protegidas no Swagger UI, faça login em `POST /auth/login`, copie o `accessToken` e clique no botão **Authorize** no topo da página informando o token.

---

## 7. Usuários de Teste (Seed V2 do Flyway)

A senha padrão de todas as contas pré-cadastradas é: `Senha@123`

| Nome | E-mail | Perfil | Uso nos Testes |
| :--- | :--- | :--- | :--- |
| **Cliente Ana Silva** | `ana.silva@email.com` | `CLIENTE` | Criar pedidos multicanal, simular pagamento e validar bloqueio 403 em auditoria |
| **Cliente Bruno Ferreira** | `bruno.ferreira@email.com` | `CLIENTE` | Segundo cliente para testes de consulta e cadastro |
| **Atendente Maria Balcao** | `atendente.derby@raizesdonordeste.com.br` | `ATENDENTE` | Avançar status do pedido no balcão/cozinha |
| **Gerente Carlos Recife** | `gerente.recife@raizesdonordeste.com.br` | `GERENTE` | Atualizar status e consultar logs de auditoria |
| **Administrador do Sistema** | `admin@raizesdonordeste.com.br` | `ADMIN` | Acesso completo aos endpoints |

---

## 8. Execução dos Testes no Postman

Importe o arquivo `postman/raizes_nordeste_postman_collection.json` no Postman e execute via **Collection Runner** (ou manualmente na ordem das pastas abaixo, pois o script de teste já salva `tokenCliente`, `tokenGerente`, `pedidoId` e `pedidoRecusaId` automaticamente):

1. **1. Auth & Segurança:** `T01` (login cliente), `T01.1` (login gerente), `T02` (401 sem token) e `T03` (403 com perfil cliente em `/auditoria`).
2. **2. Unidades e Cardápio:** `T04` (listar filiais ativas) e `T05` (consultar cardápio com saldo da filial 1).
3. **3. Pedidos & Multicanalidade:** `T06` (criar pedido via `TOTEM`), `T07` (422 sem `canalPedido`), `T07.1` (422 quantidade `-1`), `T08` (404 unidade inexistente), `T09` (409 estoque insuficiente na filial Derby) e `T10` (filtro `?canalPedido=TOTEM`).
4. **4. Pagamento Mock & Ciclo de Vida:** `T11` (aprovar pagamento -> `EM_PREPARACAO`), `T11.1` (novo pedido via `APP`), `T11.2` (recusar pagamento -> `PAGAMENTO_RECUSADO` com estorno de estoque), `T12` (mudar status para `PRONTO`) e `T13` (consultar logs em `GET /auditoria`).

---

## 9. Testes Automatizados (JUnit 5 + Mockito + MockMvc)

Os testes unitários e da camada web não dependem do Docker estar rodando:

```bash
mvn test
```

Classes implementadas em `src/test/java/br/com/uninter/raizesnordeste/`:
- `service/PedidoServiceTest`: criação de pedido com baixa de estoque, erro 409 de saldo insuficiente e erro 404 de unidade inexistente.
- `service/PagamentoServiceTest`: aprovação do pagamento mock e recusa com estorno automático de estoque.
- `service/AuthServiceTest`: login com validação BCrypt/JWT e bloqueio de cadastro sem aceite LGPD.
- `controller/PedidoControllerTest`: validação dos contratos HTTP (`201`, `422` e `409`) via `MockMvc`.

---

## 10. Padrão de Resposta de Erro

Todas as exceções tratadas pelo `GlobalExceptionHandler` seguem o mesmo formato JSON:

```json
{
  "error": "ESTOQUE_INSUFICIENTE",
  "message": "Não há quantidade suficiente para um ou mais itens.",
  "details": [
    {
      "field": "itens[0].quantidade",
      "issue": "Disponível: 2 | Solicitado: 999"
    }
  ],
  "timestamp": "2026-03-01T15:30:00Z",
  "path": "/pedidos"
}
```
