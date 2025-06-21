# Trilha de Aprendizado Java - EDUC360

Java RESTFul API criada para a simulação de um banco digital 

## Principais Tecnologias
- **Java 17**: Java 17: Utilizamos a versão LTS (Long Term Support) do Java para aproveitar as inovações e a robustez que essa linguagem oferece, garantindo performance e escalabilidade.
- **Spring Boot 3.3.1**: Trabalharemos com a mais nova versão do Spring Boot, que maximiza a produtividade do desenvolvedor por meio de sua poderosa premissa de autoconfiguração;
- **Spring Data JPA**: Exploraremos como essa ferramenta pode simplificar nossa camada de acesso aos dados, facilitando a integração com bancos de dados SQL;
- **Spring Security**: Implementamos a segurança da API utilizando o Spring Security, garantindo autenticação e autorização robustas para proteger os recursos do banco digital.
- **JWT (JSON Web Tokens)**: Para a autenticação, empregamos JWT, um método seguro e eficiente para transmitir informações entre as partes como um objeto JSON.
- **H2 Database**: Para desenvolvimento e testes, utilizamos o H2, um banco de dados relacional em memória que agiliza o ciclo de desenvolvimento.
- **Flyway**: Gerenciamos as migrações do banco de dados com o Flyway, garantindo que o esquema do banco esteja sempre sincronizado com o código da aplicação de forma controlada.
- **OpenAPI (Swagger)**: Vamos criar uma documentação de API eficaz e fácil de entender usando a OpenAPI (Swagger), perfeitamente alinhada com a alta produtividade que o Spring Boot oferece;

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
  

  User "1" *-- "1" Role
  
```

https://www.youtube.com/watch?v=nDst-CRKt_k  




