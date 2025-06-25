# Trilha de Aprendizado Java - EDUC360

Java RESTFul API criada para a simulação de um banco digital 

## Principais Tecnologias
- **Java 17**: Utilizei a versão LTS (Long Term Support) do Java para aproveitar as inovações e a robustez que essa linguagem oferece, garantindo performance e escalabilidade.
- **Spring Boot 3.3.1**: Trabalhei com a mais nova versão do Spring Boot, que maximiza a produtividade do desenvolvedor por meio de sua poderosa premissa de autoconfiguração.
- **Spring Data JPA**: Explorei como essa ferramenta pode simplificar minha camada de acesso aos dados, facilitando a integração com bancos de dados SQL.
- **Spring JDBC Template**: Para cenários que exigiam maior controle sobre as instruções SQL ou otimizações de performance, utilizei o Spring JDBC Template. Ele simplifica a interação com o banco de dados via JDBC, tratando o boilerplate de abertura e fechamento de conexões e convertendo exceções, o que me permitiu focar na lógica da query.
- **Spring Security**: Implementei a segurança da API utilizando o Spring Security, garantindo autenticação e autorização robustas para proteger os recursos do banco digital.
- **JWT (JSON Web Tokens)**: Para a autenticação, empreguei JWT, um método seguro e eficiente para transmitir informações entre as partes como um objeto JSON.
- **H2 Database**: Para desenvolvimento e testes, utilizei o H2, um banco de dados relacional em memória que agiliza o ciclo de desenvolvimento.
- **OpenAPI (Swagger)**: Criei uma documentação de API eficaz e fácil de entender usando a OpenAPI (Swagger), perfeitamente alinhada com a alta produtividade que o Spring Boot oferece.

## Link para visualizar os recursos da API

http://localhost:8080/swagger-ui/index.html


## Diagrama de Classes (Domínio da API)

```mermaid
classDiagram
  class User {
    -UUID userId
    -String username
    -String password
    -Role[] roles
  }

  class Role {
    -Long roleId
    -String name
  }
  

  User "1" *-- "1..N" Role
  
```



## Exemplo de fluxo no mermaid
```mermaid
graph TD
A[Início] --> B{Decisão?};
B -- Sim --> C[Ação 1];
B -- Não --> D[Ação 2];
C --> E[Fim];
D --> E;
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
        Controller[1. Controller]
        Service[2. Service]
        DAO[3. DAO]
        DB[(4. Banco de Dados)]
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
%% --- Estilos ---
style ErrorResponse fill:#ffcccc,stroke:#b2222ä2,stroke-width:1px
style SuccessResponse fill:#ccffcc,stroke:#006400,stroke-width:1px

    %% --- Fluxo da Requisição ---
    subgraph "Requisição do Cliente"
        Request[HTTP Request]
    end

    subgraph "Processamento no Backend"
        Controller[1. Controller]
        Service[2. Service]
        DAO[3. DAO]
        DB[(4. Banco de Dados)]
    end

    subgraph "Geração da Resposta"
        SuccessResponse[Resposta de Sucesso (2xx)]:::SuccessResponse
        ErrorResponse[Resposta de Erro (4xx/5xx)]:::ErrorResponse
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