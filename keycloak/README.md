# Keycloak Setup Guide

This directory contains the Keycloak realm configuration for the CX Invest application.

## Files

- `realm-export.json`: Complete realm configuration including roles, clients, and test users

## Quick Start

### 1. Start Keycloak

From the project root directory:

```bash
docker-compose up -d keycloak
```

Wait for Keycloak to start (about 30 seconds). You can check the logs:

```bash
docker logs -f keycloak
```

### 2. Access Keycloak Admin Console

Open your browser and navigate to:
```
http://localhost:8180
```

**Admin credentials:**
- Username: `admin`
- Password: `admin`

### 3. Verify Realm Import

The realm `cx-invest` should be automatically imported. To verify:

1. Click on the realm dropdown (top left)
2. You should see `cx-invest` in the list
3. Select it to view the configuration

### 4. Test Users

Three test users are pre-configured:

| Username | Password | Role | Email |
|----------|----------|------|-------|
| admin | admin123 | admin | admin@cxinvest.com |
| analista | analista123 | analista | analista@cxinvest.com |
| cliente | cliente123 | cliente | cliente@cxinvest.com |

## Realm Configuration Details

### Roles

- **admin**: Full system access
- **analista**: Read access to reports and data
- **cliente**: Restricted access to own resources

### Clients

#### cx-invest-backend
- Type: Bearer-only
- Used by: Backend API
- Purpose: Validates JWT tokens

#### cx-invest-frontend
- Type: Public
- Used by: Frontend applications
- Purpose: Obtains JWT tokens via Direct Grant

### Security Settings

- Token lifespan: 3600 seconds (1 hour)
- Brute force protection: Enabled
- SSL required: External (disable for development)
- Email verification: Disabled for development

## Getting a JWT Token

### Using cURL

```bash
curl -X POST 'http://localhost:8180/realms/cx-invest/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=cx-invest-frontend' \
  -d 'username=admin' \
  -d 'password=admin123' \
  -d 'grant_type=password'
```

### Using httpie

```bash
http --form POST http://localhost:8180/realms/cx-invest/protocol/openid-connect/token \
  client_id=cx-invest-frontend \
  username=admin \
  password=admin123 \
  grant_type=password
```

### Response

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 3600,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "scope": "profile email"
}
```

## Using the Token

Add the token to your API requests:

```bash
curl -X GET 'http://localhost:8080/clientes' \
  -H 'Authorization: Bearer <access_token>'
```

## Customization

### Adding New Users

1. Login to Keycloak Admin Console
2. Select `cx-invest` realm
3. Go to Users → Add User
4. Fill in the required fields
5. Go to Credentials tab and set password
6. Go to Role Mappings tab and assign roles

### Adding New Roles

1. Go to Realm Roles
2. Click "Create Role"
3. Enter role name and description
4. Save

### Modifying Token Lifespan

1. Go to Realm Settings → Tokens
2. Adjust "Access Token Lifespan"
3. Save

## Production Considerations

### Security Recommendations

1. **Change Default Passwords**: Update all default passwords
2. **Enable SSL**: Configure SSL certificates for HTTPS
3. **Email Configuration**: Set up SMTP for password reset
4. **Backup**: Regular backup of Keycloak database
5. **Monitoring**: Set up logging and monitoring

### Environment Variables

For production deployment, use environment variables:

```yaml
services:
  keycloak:
    image: quay.io/keycloak/keycloak:23.0.0
    environment:
      - KEYCLOAK_ADMIN=${KEYCLOAK_ADMIN}
      - KEYCLOAK_ADMIN_PASSWORD=${KEYCLOAK_ADMIN_PASSWORD}
      - KC_DB=postgres
      - KC_DB_URL=${KC_DB_URL}
      - KC_DB_USERNAME=${KC_DB_USERNAME}
      - KC_DB_PASSWORD=${KC_DB_PASSWORD}
      - KC_HOSTNAME=${KC_HOSTNAME}
      - KC_HOSTNAME_STRICT=true
      - KC_HTTP_ENABLED=false
      - KC_HTTPS_ENABLED=true
```

### Database

For production, use PostgreSQL instead of dev-file:

```yaml
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: keycloak
      POSTGRES_USER: keycloak
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data

  keycloak:
    depends_on:
      - postgres
    environment:
      - KC_DB=postgres
      - KC_DB_URL=jdbc:postgresql://postgres:5432/keycloak
```

## Troubleshooting

### Keycloak doesn't start

```bash
# Check logs
docker logs keycloak

# Restart container
docker-compose restart keycloak
```

### Port 8180 already in use

Edit `docker-compose.yml` and change the port mapping:
```yaml
ports:
  - "8181:8080"  # Changed from 8180
```

### Realm not imported

Manual import:
1. Login to Admin Console
2. Click "Add realm"
3. Click "Import" and select `realm-export.json`

### Cannot get token

- Verify Keycloak is running: `curl http://localhost:8180/health`
- Check user credentials
- Verify client_id is correct (`cx-invest-frontend`)

## Useful Links

- Keycloak Admin Console: http://localhost:8180
- Realm configuration: http://localhost:8180/admin/master/console/#/cx-invest/realm-settings
- Users: http://localhost:8180/admin/master/console/#/cx-invest/users
- Clients: http://localhost:8180/admin/master/console/#/cx-invest/clients

## Support

For more information about Keycloak:
- [Official Documentation](https://www.keycloak.org/documentation)
- [Getting Started Guide](https://www.keycloak.org/guides#getting-started)
- [Server Administration Guide](https://www.keycloak.org/docs/latest/server_admin/)
