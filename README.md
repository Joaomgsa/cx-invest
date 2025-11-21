# cx-invest - Plataforma de Recomendação de Investimentos

As definiçoes de API, arquitetura e segurança estão detalhadas no arquivo `projeto.md`.

Este projeto utiliza Quarkus, o framework Java Supersônico e Subatômico.

Se quiser saber mais sobre Quarkus, visite: <https://quarkus.io/>.

## Documentação do projeto

As definições e a documentação de arquitetura do projeto estão no arquivo `projeto.md` na raiz do repositório. Consulte esse arquivo para detalhes sobre Estrutura da API, Motor de Recomendação, Segurança e Testes.

## Segurança

Esta aplicação utiliza o Keycloak para autenticação e autorização com controle por papéis (RBAC).

**Quick Start com Segurança:**

```powershell
# Iniciar o Keycloak (via docker-compose, se configurado)
docker-compose up -d keycloak

# Aguardar o Keycloak ficar pronto (ver logs)
docker logs -f keycloak

# Executar a aplicação em modo dev
./mvnw quarkus:dev
```

**Usuários de teste:**
- Admin: `admin` / `admin123` (acesso completo)
- Analista: `analista` / `analista123` (acesso de leitura para relatórios)
- Cliente: `cliente` / `cliente123` (acesso restrito aos próprios recursos)

Para a documentação completa sobre segurança, veja `SECURITY.md`.

## Executando a aplicação em modo dev

Você pode executar a aplicação no modo de desenvolvimento (live coding) com:

```powershell
./mvnw quarkus:dev
```

> **OBS:** A Dev UI do Quarkus fica disponível somente em modo dev em <http://localhost:8080/q/dev/>.

## Empacotando e executando a aplicação

Empacete a aplicação:

```powershell
./mvnw package
```

Isso produz o arquivo `quarkus-run.jar` em `target/quarkus-app/`.

Execute com:

```powershell
java -jar target/quarkus-app/quarkus-run.jar
```

Para criar um "über-jar":

```powershell
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

O artefato gerado estará em `target/*-runner.jar`.

## Gerando um executável nativo

```powershell
./mvnw package -Dnative
```

Ou em container (se não tiver GraalVM localmente):

```powershell
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Execute o binário nativo:

```powershell
./target/cx-invest-1.0.0-SNAPSHOT-runner
```

## Guias relacionados

- OpenAPI Generator - REST Client Generator: https://docs.quarkiverse.io/quarkus-openapi-generator/dev/index.html
- REST (Quarkus): https://quarkus.io/guides/rest
- OpenTelemetry: https://quarkus.io/guides/opentelemetry
- Hibernate ORM com Panache: https://quarkus.io/guides/hibernate-orm-panache
- SmallRye JWT: https://quarkus.io/guides/security-jwt

## Código fornecido

### Hibernate ORM

Crie sua primeira entidade JPA — ver guias do Quarkus acima.

### OpenAPI Generator Client Codestart

Veja o guia do OpenAPI Generator para clientes/servidor se precisar gerar código.

## Requisitos

Certifique-se de ter as extensões necessárias adicionadas ao projeto. Exemplos:

Para adicionar OpenAPI (SmallRye OpenAPI):

```powershell
# Quarkus CLI
quarkus ext add io.quarkus:quarkus-smallrye-openapi

# Ou via Maven
./mvnw quarkus:add-extension -Dextensions="io.quarkus:quarkus-smallrye-openapi"
```

Para adicionar o REST Client Jackson (se necessário):

```powershell
./mvnw quarkus:add-extension -Dextensions="io.quarkus:quarkus-rest-client-jackson"
```

## Executando testes (JDK 21)

Este projeto é ajustado para Java 21. Antes de executar, ajuste `JAVA_HOME` para o JDK 21.

Verifique versões:

```powershell
mvn -v
java -version
```

Executar em modo dev:

```powershell
./mvnw quarkus:dev
```

Executar todos os testes:

```powershell
mvn test
```

Executar apenas testes de integração (se aplicável):

```powershell
mvn -Dtest=*IntegrationTest test
```

---


