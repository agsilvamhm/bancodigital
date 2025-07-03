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
    -double taxaManutencaoMensal
  }

  class ContaPoupanca {
    -double taxaRendimentoMensal
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
%% Fluxo da Operação de Depósito
graph TD
    subgraph "Início da Requisição"
        A[POST /contas/{id}/deposito com DTO(valor)]
    end

    subgraph "Camada de Serviço (@Transactional)"
        B{Valor do depósito é positivo?}
        B -- Não --> C[Lança Erro de Validação<br/>(IllegalArgumentException)]
        B -- Sim --> D[Busca conta de destino pelo ID]
        D --> E{Conta de destino existe?}
        E -- Não --> F[Lança Erro<br/>(EntidadeNaoEncontradaException)]
        E -- Sim --> G((Início da Transação))
        G --> H[1. Adiciona valor ao saldo da conta]
        H --> I[2. Atualiza a conta no banco (UPDATE)]
        I --> J[3. Cria registro de Movimentacao<br/>(tipo=DEPOSITO, origem=null)]
        J --> K[4. Salva a movimentação no banco (INSERT)]
        K --> L((Fim da Transação))
    end

    subgraph "Fim da Requisição"
        L -- Commit --> M((Sucesso<br/>Retorna 200 OK com 'Recibo'))
        C --> Z((Falha<br/>Retorna Erro 422))
        F --> Z
        %% Qualquer falha dentro da transação leva ao Rollback
        H -- Falha no DB --> R((Rollback))
        I -- Falha no DB --> R
        J -- Falha no DB --> R
        K -- Falha no DB --> R
        R --> Y((Falha<br/>Retorna Erro 500))
    end
    
    style M fill:#d4edda,stroke:#c3e6cb
    style Z fill:#f8d7da,stroke:#f5c6cb
    style Y fill:#f8d7da,stroke:#f5c6cb
```