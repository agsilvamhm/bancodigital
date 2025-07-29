# Trilha de Aprendizado Java - EDUC360

# API para Banco Digital

Este projeto consiste no desenvolvimento de uma API RESTful robusta para um sistema de Banco Digital.
A aplicação simula as operações essenciais de uma instituição financeira, permitindo o gerenciamento 
completo de clientes, contas, cartões e seguros, com regras de negócio bem definidas para cada funcionalidade.

## Principais Funcionalidades

O sistema foi projetado para cobrir as seguintes áreas:

* **Autenticação:**
* O sistema utilizará o controle de autenticação por token JWT, como também fará o registro dos acessos ao sistema para cada usuário.

```mermaid
classDiagram
  direction LR

  class User {
    -UUID userId
    -String username
    -String password
    -UserStatus status
  }

  class Role {
    -Long roleId
    -String name
  }

  class Permission {
    -Long permissionId
    -String name  // ex: "CREATE_USER", "READ_REPORTS"
  }

  class AuditEvent {
    -Long eventId
    -Date timestamp
    -AuditEventType type
    -String ipAddress
    -Boolean success
  }

  class Notification {
    -Long notificationId
    -String message
    -Date createdAt
    -Boolean isRead
    -NotificationType type
    -String link
  }

  class UserStatus {
    <<enumeration>>
    ACTIVE
    INACTIVE
    PENDING_VERIFICATION
    BANNED
  }
  
  class AuditEventType {
    <<enumeration>>
    LOGIN_SUCCESS
    LOGIN_FAILURE
    LOGOUT
    PASSWORD_UPDATE
    RESOURCE_ACCESS_DENIED
  }

  class NotificationType {
    <<enumeration>>
    SYSTEM_ALERT
    DIRECT_MESSAGE
    TASK_ASSIGNMENT
  }

  User "1" -- "0..*" Role : possui
  Role "*" -- "*" Permission : contém
  User "1" -- "0..*" AuditEvent : gerou
  User "1" -- "0..*" Notification : destinatário    
```

* **👤 Gestão de Clientes:**

    * Cadastro, consulta, atualização e exclusão de clientes (CRUD).
    * Classificação de clientes em três categorias: **Comum**, **Super** e **Premium**, que define o acesso a diferentes benefícios e taxas.
    * Validação rigorosa de dados na entrada, como formato e unicidade do CPF, idade: o cliente dever ser maior ou igual a 18 anos e obedecer o formato "DD/MM/AAAA", o cep deve obedecer o formato "XXXXX-XXX".

* **👤 Contas Bancárias:**

    * Abertura de **Conta Corrente** e **Conta Poupança**.
    * Operações essenciais como consulta de saldo, depósito, saque e transferências (incluindo Pix).
    * Lógica automatizada para desconto de **taxa de manutenção** (conta corrente) e aplicação de **rendimentos mensais** (conta poupança), com valores diferenciados por categoria de cliente.

* **💳 Gestão de Cartões:**

    * Emissão de cartões de **Crédito** e **Débito** vinculados às contas.
    * Funcionalidades para realizar pagamentos, alterar status (ativo/inativo), trocar senha e consultar faturas.
    * **Cartão de Crédito:** Possui limite pré-aprovado de acordo com a categoria do cliente e bloqueio automático de novas compras ao atingir o limite.
    * **Cartão de Débito:** Possui um limite de transação diário que pode ser ajustado pelo usuário.

* **🛡️ Seguros e Apólices:**

    * Contratação de seguros específicos para cartões de crédito.
    * Geração automática de apólices eletrônicas com número único, detalhes da cobertura e condições.

## 🛠️ Arquitetura da API

A API é RESTful e foi estruturada em torno dos principais recursos do sistema. Abaixo estão os endpoints planejados para cada módulo.

<details>
<summary><strong>👤 Endpoints de Cliente</strong></summary>

- `POST /clientes` - Criar um novo cliente
- `GET /clientes` - Listar todos os clientes
- `GET /clientes/{id}` - Obter detalhes de um cliente
- `PUT /clientes/{id}` - Atualizar informações de um cliente
- `DELETE /clientes/{id}` - Remover um cliente

</details>

<details>
<summary><strong>👤 Endpoints de Conta</strong></summary>

- `POST /contas` - Criar uma nova conta
- `GET /contas/{id}` - Obter detalhes de uma conta
- `GET /contas/{id}/saldo` - Consultar saldo da conta
- `POST /contas/{id}/deposito` - Realizar um depósito na conta
- `POST /contas/{id}/saque` - Realizar um saque da conta
- `POST /contas/{id}/transferencia` - Realizar uma transferência entre contas
- `POST /contas/{id}/pix` - Realizar um pagamento via Pix
- `PUT /contas/{id}/manutencao` - Aplicar taxa de manutenção (conta corrente)
- `PUT /contas/{id}/rendimentos` - Aplicar rendimentos (conta poupança)

</details>

<details>
<summary><strong>💳 Endpoints de Cartão</strong></summary>

- `POST /cartoes` - Emitir um novo cartão
- `GET /cartoes/{id}` - Obter detalhes de um cartão
- `POST /cartoes/{id}/pagamento` - Realizar um pagamento com o cartão
- `GET /cartoes/{id}/fatura` - Consultar fatura do cartão de crédito
- `POST /cartoes/{id}/fatura/pagamento` - Realizar pagamento da fatura
- `PUT /cartoes/{id}/senha` - Alterar senha do cartão
- `PUT /cartoes/{id}/status` - Ativar ou desativar um cartão
- `PUT /cartoes/{id}/limite` - Alterar limite do cartão de crédito
- `PUT /cartoes/{id}/limite-diario` - Alterar limite diário do cartão de débito

</details>

<details>
<summary><strong>🛡️ Endpoints de Seguro</strong></summary>

- `POST /seguros` - Contratar um seguro
- `GET /seguros` - Listar todos os seguros disponíveis
- `GET /seguros/{id}` - Obter detalhes de uma apólice de seguro
- `PUT /seguros/{id}/cancelar` - Cancelar uma apólice de seguro

</details>

## 🚀 Funcionalidades Adicionais

* **🔒 Autenticação e Autorização:** Implementação de `Spring Security` com `JWT` para proteger os endpoints e definir níveis de acesso (`ROLE_ADMIN`, `ROLE_CLIENTE`).

## Tecnologias utilizadas
- **Java 17**: Utilizei a versão LTS (Long Term Support) do Java para aproveitar as inovações e a robustez que essa linguagem oferece, garantindo performance e escalabilidade.
- **Spring Boot 3.3.1**: Trabalhei com a mais nova versão do Spring Boot, que maximiza a produtividade do desenvolvedor por meio de sua poderosa premissa de autoconfiguração.
- **Spring Data JPA**: Explorei como essa ferramenta pode simplificar minha camada de acesso aos dados, facilitando a integração com bancos de dados SQL.
- **Spring JDBC Template**: Para cenários que exigiam maior controle sobre as instruções SQL ou otimizações de performance, utilizei o Spring JDBC Template. Ele simplifica a interação com o banco de dados via JDBC, tratando o boilerplate de abertura e fechamento de conexões e convertendo exceções, o que me permitiu focar na lógica da query.
- **Spring Security**: Implementei a segurança da API utilizando o Spring Security, garantindo autenticação e autorização robustas para proteger os recursos do banco digital.
- **JWT (JSON Web Tokens)**: Para a autenticação, empreguei JWT, um método seguro e eficiente para transmitir informações entre as partes como um objeto JSON.
- **H2 Database**: Para desenvolvimento e testes, utilizei o H2, um banco de dados relacional em memória que agiliza o ciclo de desenvolvimento.
- **OpenAPI (Swagger)**: Criei uma documentação de API eficaz e fácil de entender usando a OpenAPI (Swagger), perfeitamente alinhada com a alta produtividade que o Spring Boot oferece.
- **JUnit 5**: Para garantir a robustez e a qualidade do código, adotei o JUnit 5 para a criação de testes unitários e de integração. 

## Link para visualizar os recursos da API

http://localhost:8080/swagger-ui/index.html


## Diagrama de Classes (Domínio da Aplicação)

```mermaid
classDiagram
  direction LR

  class Cliente {
    -Integer id
    -String cpf
    -String nome
    -LocalDate dataNascimento
    -Endereco endereco
    -CategoriaCliente categoria 
    -List~Conta~ contas 
  }

  class CategoriaCliente {
      <<enumeration>>
      COMUM
      SUPER
      PREMIUM
  }
  
  class Endereco {
    -Integer id
    -String rua
    -Integer numero
    -String complemento
    -String cidade
    -String estado
    -String cep 
  }

  class Conta {
    <<Abstract>>
    #Integer id
    #String numero
    #String agencia
    #double saldo
    #Cliente cliente
    #List~Movimentacao~ movimentacoes
    #List~Cartao~ cartoes
  }

  class ContaCorrente {
   }

  class ContaPoupanca {
  }

  class Movimentacao {
    -Integer id
    -TipoMovimentacao tipo
    -double valor
    -LocalDateTime dataHora
    -Conta contaOrigem
    -Conta contaDestino
    -String descricao
  }

  class TipoMovimentacao {
      <<enumeration>>
      DEPOSITO
      SAQUE
      TRANSFERENCIA
      PIX
   }

  class Cartao {
    -Integer id
    -Conta conta
    -String numero
    -String nomeTitular
    -LocalDate dataValidade
    -String cvv
    -String senha
    -TipoCartao tipoCartao
    -BigDecimal limiteCredito
    -BigDecimal limiteDiarioDebito
    -boolean ativo
  }

  class TipoCartao {
    <<enumeration>>
    CREDITO
    DEBITO
  }

  class SeguroCartao {
    -Integer id
    -Cartao cartao
    -String numeroApolice
    -LocalDateTime dataContratacao
    -String cobertura
    -String condicoes
    -BigDecimal valorPremio
  }

  Cliente "1" -- "1" Endereco : possui
  Cliente "1" -- "1" CategoriaCliente : é da categoria
  Cliente "1" -- "1..N" Conta : possui
  Conta <|-- ContaCorrente
  Conta <|-- ContaPoupanca
  Conta "1" -- "0..N" Movimentacao : registra
  Conta "1" -- "0..N" Cartao : possui
  Movimentacao "1" -- "1" TipoMovimentacao : é do tipo
  Cartao "1" -- "1" TipoCartao : é do tipo
  Cartao "1" -- "0..1" SeguroCartao : pode ter  
```

## Fluxo de Camadas (Visão Estrutural)

```mermaid
graph LR
    %% --- Estilos para os nós ---
    style DB fill:#d5d5d5,stroke:#333,stroke-width:2px
    style Ex fill:#ffcccc,stroke:#b22222,stroke-width:1px
    style Success fill:#ccffcc,stroke:#006400,stroke-width:1px

    %% --- Fluxo Principal ---
    DB[(Tabela do<br>Banco de Dados)] --> POJO[Classe POJO]
    POJO --> DAO[Classe DAO]
    DAO --> Service[Classe Service]
    Service --> Controller[Classe Controller]

    %% --- Expansão da Camada DAO ---
    DAO -- Retorna --> DaoSuccess["Mensagem de Sucesso"]:::Success
    DAO -- Lança --> DaoException["Exceção de Acesso<br>a Dados"]:::Ex

    %% --- Expansão da Camada Service ---
    Service -- Retorna --> ServiceSuccess["Mensagem de Sucesso"]:::Success
    Service -- Lança --> ServiceException["Exceção de Regra<br>de Negócio"]:::Ex

    %% --- Expansão da Camada Controller (Resposta da Requisição) ---
    Controller -- Gera Resposta --> HttpResponse["Retorno de Sucesso<br>HTTP (2xx)"]:::Success
```


## Fluxo de Requisição (Visão de Processamento)

```mermaid
graph TD
    %% --- Estilos ---
    style ErrorResponse fill:#ffcccc,stroke:#b22222,stroke-width:1px
    style SuccessResponse fill:#ccffcc,stroke:#006400,stroke-width:1px
    
    %% --- Fluxo da Requisição ---
    subgraph "Requisição do Cliente"
        Request[HTTP Request]
    end

    subgraph "Processamento no Backend"
        Controller[Controller]
        Service[Service]
        DAO[DAO]
        DB[(Banco de Dados)]
    end

    subgraph "Geração da Resposta"
        SuccessResponse["Resposta de Sucesso (2xx)"]:::SuccessResponse
        ErrorResponse["Resposta de Erro (4xx/5xx)"]:::ErrorResponse
    end

    %% --- Conexões do Fluxo ---
    Request --> Controller
    Controller -->|Valida e chama| Service
    Service -->|Aplica regras e chama| DAO
    DAO -->|Executa a query| DB

    %% --- Caminho de Sucesso ---
    DB -- Retorna dados --> DAO
    DAO -- Retorna POJO/DTO --> Service
    Service -- Retorna DTO --> Controller
    Controller -->|Monta a resposta| SuccessResponse

    %% --- Caminho de Exceção ---
    DAO -- Lança Exceção de Dados --> Service
    Service -- Lança Exceção de Negócio --> Controller
    Controller -- Captura Exceções --> ErrorResponse
```

```mermaid
graph TD
    A[Inicio] --> B{Receber dados do Cliente};

    B --> C{CPF eh nulo ou vazio};
    C -- Sim --> E[Retornar Erro: CPF obrigatorio];
    C -- Nao --> D{CPF eh valido};
    D -- Nao --> F[Retornar Erro: CPF invalido];
    D -- Sim --> G{CPF ja existe};
    G -- Sim --> H[Retornar Erro: CPF ja cadastrado];
    G -- Nao --> I{Nome eh nulo ou vazio};
    I -- Sim --> J[Retornar Erro: Nome obrigatorio];
    I -- Nao --> K{Nome tem entre 2 e 100 caracteres};
    K -- Nao --> L[Retornar Erro: Nome fora do tamanho permitido];
    K -- Sim --> M{Nome contem apenas letras e espacos};
    M -- Nao --> N[Retornar Erro: Nome invalido];
    M -- Sim --> O{Data de Nascimento eh nula ou vazia};
    O -- Sim --> P[Retornar Erro: Data de nascimento obrigatoria];
    O -- Nao --> Q{Data de Nascimento no formato DD/MM/AAAA};
    Q -- Nao --> R[Retornar Erro: Formato de Data de Nascimento invalido];
    Q -- Sim --> S{Cliente tem 18 anos ou mais};
    S -- Nao --> T[Retornar Erro: Cliente menor de idade];
    S -- Sim --> U{Endereco eh nulo};
    U -- Sim --> V[Retornar Erro: Endereco obrigatorio];
    U -- Nao --> W{Rua, Numero, Cidade, Estado e CEP estao preenchidos};
    W -- Nao --> X[Retornar Erro: Endereco incompleto];
    W -- Sim --> Y{CEP esta no formato XXXXX-XXX};
    Y -- Nao --> Z[Retornar Erro: CEP invalido];
    Y -- Sim --> AA[Dados do Cliente Validos];

    AA --> B_End[Fim];

    E --> B_End;
    F --> B_End;
    H --> B_End;
    J --> B_End;
    L --> B_End;
    N --> B_End;
    P --> B_End;
    R --> B_End;
    T --> B_End;
    V --> B_End;
    X --> B_End;
    Z --> B_End;
```