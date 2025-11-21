# Instruções para rodar em DevMode dentro de container (WSL/Ubuntu)

Essas instruções assumem que você está no Windows e vai usar `wsl` para abrir um shell Ubuntu onde o Docker está instalado e funcionando.

1) Abra o WSL (Ubuntu) no diretório do projeto:

```powershell
# no Windows PowerShell, execute:
wsl
# dentro do WSL, navegue para o diretório montado do projeto
cd /mnt/c/Users/joaom/OneDrive/Documentos/dev/desafio-psi/cx-invest
```

2) Subir os serviços infra (keycloak, prometheus, grafana) usando o docker-compose principal (se ainda não subiu):

```bash
# dentro do WSL/Ubuntu
docker compose up -d keycloak prometheus grafana
```

3) Subir o Quarkus em dev mode via `docker-compose.dev.yml`:

```bash
# dentro do WSL/Ubuntu, ainda na raiz do projeto
docker compose -f docker-compose.dev.yml up --build
```

Observações importantes:
- A opção `./mvnw quarkus:dev` será executada dentro do container; garanta que o arquivo `mvnw` esteja presente e executável (o comando `chmod +x mvnw` no compose tenta ajustar).
- O `~/.m2` do host é montado para o container para acelerar downloads de dependências.
- A aplicação ficará disponível em `http://localhost:8081` (mapeamento local -> container 8080).
- Se estiver no Windows e alterações não forem detectadas, o parâmetro `-Dquarkus.live-reload.polling=true` está ativado no `docker-compose.dev.yml` para forçar o polling.

Debug:
- Para ativar debug remoto, conecte seu IDE à porta 5005 (o `docker-compose.dev.yml` já expõe 5005:5005).

Parar:

```bash
# parar e remover apenas o serviço dev
docker compose -f docker-compose.dev.yml down

# parar tudo
docker compose down
```

