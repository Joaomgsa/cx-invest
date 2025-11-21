# Segurança com Keycloak - CX Invest

Este documento descreve a implementação de autenticação e autorização baseada em papéis (roles) usando Keycloak no sistema CX Invest.

## Visão Geral

O sistema utiliza Keycloak como provedor de identidade (IdP) com integração OIDC (OpenID Connect) para:
- **Autenticação**: Validação de tokens JWT
- **Autorização**: Controle de acesso baseado em papéis (RBAC)

## Papéis (Roles)

O sistema define três papéis principais:

### 1. Admin
**Acesso**: Total a todos os endpoints e funcionalidades
- Gerenciamento completo de clientes (CRUD)
- Gerenciamento completo de produtos (CRUD)
- Gerenciamento completo de perfis (CRUD)
- Acesso a relatórios e telemetria
- Acesso a simulações e históricos

### 2. Analista
**Acesso**: Leitura de dados e acesso a relatórios
- Leitura de clientes
- Leitura de produtos
- Leitura de perfis
- Acesso a relatórios de simulações
- Acesso a telemetria
- Acesso a históricos de investimentos

### 3. Cliente
**Acesso**: Restrito aos próprios recursos
- Visualização dos próprios dados
- Atualização dos próprios dados
- Acesso a produtos e perfis (leitura)
- Simulação de investimentos
- Visualização do próprio histórico de investimentos
- Visualização do próprio perfil de risco

## Matriz de Permissões por Endpoint

| Endpoint | Método | Admin | Analista | Cliente |
|----------|--------|-------|----------|---------|
| `/clientes` | GET | ✅ | ✅ | ❌ |
| `/clientes/{id}` | GET | ✅ | ✅ | ✅* |
| `/clientes` | POST | ✅ | ❌ | ❌ |
| `/clientes/{id}` | PUT | ✅ | ❌ | ✅* |
| `/clientes/{id}` | DELETE | ✅ | ❌ | ❌ |
| `/produtos` | GET | ✅ | ✅ | ✅ |
| `/produtos/{id}` | GET | ✅ | ✅ | ✅ |
| `/produtos` | POST | ✅ | ❌ | ❌ |
| `/produtos/{id}` | PUT | ✅ | ❌ | ❌ |
| `/produtos/{id}` | DELETE | ✅ | ❌ | ❌ |
| `/perfis` | GET | ✅ | ✅ | ✅ |
| `/perfis/{id}` | GET | ✅ | ✅ | ✅ |
| `/perfis` | POST | ✅ | ❌ | ❌ |
| `/perfis/{id}` | PUT | ✅ | ❌ | ❌ |
| `/perfis/{id}` | DELETE | ✅ | ❌ | ❌ |
| `/simulacoes/simular-investimento` | POST | ✅ | ✅ | ✅ |
| `/simulacoes/por-produto-dia` | GET | ✅ | ✅ | ❌ |
| `/simulacoes` | GET | ✅ | ✅ | ❌ |
| `/investimentos/{clienteId}` | GET | ✅ | ✅ | ✅* |
| `/perfil-risco/{clienteId}` | GET | ✅ | ✅ | ✅* |
| `/produtos-recomendados/{perfilId}` | GET | ✅ | ✅ | ✅ |
| `/telemetria` | GET | ✅ | ✅ | ❌ |

*\* Cliente tem acesso apenas aos próprios recursos (validação por ID)*

## Configuração do Keycloak

### 1. Iniciando o Keycloak

O Keycloak está configurado no `docker-compose.yml`:

```bash
docker-compose up -d keycloak
```

O Keycloak estará disponível em: `http://localhost:8180`

**Credenciais de Admin:**
- Username: `admin`
- Password: `admin`

### 2. Realm e Client

O realm `cx-invest` é importado automaticamente com:
- **Realm**: `cx-invest`
- **Client Backend**: `cx-invest-backend` (bearer-only)
- **Client Frontend**: `cx-invest-frontend` (public client)

### 3. Usuários de Teste

O sistema inclui três usuários pré-configurados:

#### Admin
- Username: `admin`
- Password: `admin123`
- Email: `admin@cxinvest.com`
- Role: `admin`

#### Analista
- Username: `analista`
- Password: `analista123`
- Email: `analista@cxinvest.com`
- Role: `analista`

#### Cliente
- Username: `cliente`
- Password: `cliente123`
- Email: `cliente@cxinvest.com`
- Role: `cliente`

## Obtendo um Token JWT

### 1. Via cURL (Direct Access Grant)

```bash
# Token para Admin
curl -X POST 'http://localhost:8180/realms/cx-invest/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=cx-invest-frontend' \
  -d 'username=admin' \
  -d 'password=admin123' \
  -d 'grant_type=password'

# Token para Analista
curl -X POST 'http://localhost:8180/realms/cx-invest/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=cx-invest-frontend' \
  -d 'username=analista' \
  -d 'password=analista123' \
  -d 'grant_type=password'

# Token para Cliente
curl -X POST 'http://localhost:8180/realms/cx-invest/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=cx-invest-frontend' \
  -d 'username=cliente' \
  -d 'password=cliente123' \
  -d 'grant_type=password'
```

A resposta incluirá:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 3600,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer"
}
```

### 2. Usando o Token

Use o `access_token` no header de autorização:

```bash
curl -X GET 'http://localhost:8080/clientes' \
  -H 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...'
```

## Exemplos de Uso

### Exemplo 1: Admin listando todos os clientes

```bash
# Obter token
TOKEN=$(curl -s -X POST 'http://localhost:8180/realms/cx-invest/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=cx-invest-frontend' \
  -d 'username=admin' \
  -d 'password=admin123' \
  -d 'grant_type=password' | jq -r '.access_token')

# Listar clientes
curl -X GET 'http://localhost:8080/clientes' \
  -H "Authorization: Bearer $TOKEN"
```

**Resultado**: ✅ Sucesso (200 OK)

### Exemplo 2: Analista tentando criar um cliente

```bash
# Obter token
TOKEN=$(curl -s -X POST 'http://localhost:8180/realms/cx-invest/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=cx-invest-frontend' \
  -d 'username=analista' \
  -d 'password=analista123' \
  -d 'grant_type=password' | jq -r '.access_token')

# Tentar criar cliente
curl -X POST 'http://localhost:8080/clientes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "nome": "Novo Cliente",
    "email": "novo@example.com",
    "perfilId": 1,
    "totalInvestido": 10000.0,
    "frequenciaInvestimento": "MENSAL",
    "preferenciaInvestimento": "CONSERVADOR"
  }'
```

**Resultado**: ❌ Acesso Negado (403 Forbidden)

### Exemplo 3: Cliente simulando investimento

```bash
# Obter token
TOKEN=$(curl -s -X POST 'http://localhost:8180/realms/cx-invest/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=cx-invest-frontend' \
  -d 'username=cliente' \
  -d 'password=cliente123' \
  -d 'grant_type=password' | jq -r '.access_token')

# Simular investimento
curl -X POST 'http://localhost:8080/simulacoes/simular-investimento' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "clienteId": 1,
    "valor": 10000.0,
    "prazoMeses": 12,
    "tipoProduto": "CDB"
  }'
```

**Resultado**: ✅ Sucesso (200 OK)

### Exemplo 4: Cliente tentando acessar relatórios

```bash
# Obter token
TOKEN=$(curl -s -X POST 'http://localhost:8180/realms/cx-invest/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=cx-invest-frontend' \
  -d 'username=cliente' \
  -d 'password=cliente123' \
  -d 'grant_type=password' | jq -r '.access_token')

# Tentar acessar telemetria
curl -X GET 'http://localhost:8080/telemetria' \
  -H "Authorization: Bearer $TOKEN"
```

**Resultado**: ❌ Acesso Negado (403 Forbidden)

## Testes de Segurança

O projeto inclui testes automatizados para validar as permissões:

```bash
mvn test -Dtest=*SecurityTest
```

Testes incluídos:
- `ClienteResourceSecurityTest`: Valida permissões de endpoints de clientes
- `ProdutoResourceSecurityTest`: Valida permissões de endpoints de produtos

## Configuração para Produção

### Variáveis de Ambiente

Para produção, configure as seguintes variáveis:

```properties
# Keycloak
QUARKUS_OIDC_AUTH_SERVER_URL=https://keycloak.seu-dominio.com/realms/cx-invest
QUARKUS_OIDC_CLIENT_ID=cx-invest-backend
QUARKUS_OIDC_CREDENTIALS_SECRET=<secret-gerado-pelo-keycloak>
QUARKUS_OIDC_TLS_VERIFICATION=required
```

### Recomendações de Segurança - (Para Versão de Produção em novas releases)

1. **Tokens**: Configure expiração adequada (ex: 15-30 minutos)
2. **Refresh Tokens**: Use para renovação sem re-autenticação
3. **HTTPS**: Sempre use HTTPS em produção
4. **Secrets**: Nunca compartilhe secrets em código
5. **Rate Limiting**: Implemente limite de requisições
6. **Logging**: Monitore tentativas de acesso não autorizado
7. **Revogação**: Implemente mecanismo de revogação de tokens
8. **MFA**: Considere Multi-Factor Authentication para admin

## Troubleshooting

### Erro 401 Unauthorized
- Verifique se o token está no formato correto: `Bearer <token>`
- Verifique se o token não expirou
- Confirme que o Keycloak está acessível

### Erro 403 Forbidden
- Verifique se o usuário tem o role correto
- Confirme que o role está mapeado no token JWT
- Verifique logs da aplicação para detalhes

### Keycloak não inicia
- Verifique se a porta 8180 está disponível
- Confirme que o Docker está rodando
- Verifique logs: `docker logs keycloak`

## Referências

- [Quarkus Security Guide](https://quarkus.io/guides/security)
- [Quarkus OIDC Guide](https://quarkus.io/guides/security-openid-connect)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [JWT.io](https://jwt.io/) - Para debug de tokens JWT
