# Identity-Service Configuration Guide

## Quick Reference

### Minimum Configuration (Development)
```yaml
# application.yaml
spring:
  application:
    name: identity-service
  datasource:
    url: jdbc:mysql://localhost:3306/user_db
    username: root
    password: Nnn@2002
  jpa:
    hibernate:
      ddl-auto: update

server:
  port: 9898

jwt:
  secret: ${JWT_SECRET:5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437}
  expiration: ${JWT_EXPIRATION:1800000}
```

---

## Complete Configuration Reference

### 1. Database Configuration

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/user_db
    username: root
    password: ${DB_PASSWORD:Nnn@2002}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
  
  jpa:
    hibernate:
      ddl-auto: update  # Options: validate, update, create, create-drop
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
        use_sql_comments: true
```

**Environment Variables for Different Environments:**

```bash
# Development
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/user_db"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="Nnn@2002"

# Staging
export SPRING_DATASOURCE_URL="jdbc:mysql://staging-db.internal:3306/user_db"
export SPRING_DATASOURCE_USERNAME="app_user"
export SPRING_DATASOURCE_PASSWORD="staging_pass_here"

# Production
export SPRING_DATASOURCE_URL="jdbc:mysql://prod-db.internal:3306/user_db"
export SPRING_DATASOURCE_USERNAME="prod_app_user"
export SPRING_DATASOURCE_PASSWORD="prod_secure_password_here"
```

---

### 2. JWT Configuration

```yaml
jwt:
  # Secret key for signing JWT tokens
  # Minimum 32 characters for HS256
  # Use strong random string in production
  secret: ${JWT_SECRET:5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437}
  
  # Token expiration time in milliseconds
  # 1800000 = 30 minutes
  # 3600000 = 1 hour
  # 86400000 = 1 day
  expiration: ${JWT_EXPIRATION:1800000}
```

**Generate Secure Secret:**

```bash
# Using OpenSSL (Linux/Mac)
openssl rand -hex 32

# Using PowerShell (Windows)
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((1..32 | ForEach-Object {[char][byte](Get-Random -Minimum 33 -Maximum 127)}) -join ''))

# Example secure secrets:
# 5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
# a7b8c9d0e1f2g3h4i5j6k7l8m9n0o1p2q3r4s5t6u7v8w9x0y1z2a3b4c5d6e7
```

**Expiration Times:**

| Duration | Milliseconds | Use Case |
|----------|-------------|----------|
| 15 minutes | 900000 | High-security endpoints |
| 30 minutes | 1800000 | Standard (recommended) |
| 1 hour | 3600000 | Less critical features |
| 24 hours | 86400000 | Remember-me functionality |

---

### 3. Server Configuration

```yaml
server:
  port: 9898
  servlet:
    context-path: /
    session:
      cookie:
        http-only: true    # Prevent JavaScript access
        secure: true       # HTTPS only
        same-site: strict  # CSRF protection
        max-age: 1800      # 30 minutes
  
  # For HTTPS (Production)
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: tomcat
```

---

### 4. Security Configuration

```yaml
spring:
  security:
    user:
      name: admin
      password: admin
```

**In Code (AuthConfig.java):**

```java
// Security filter chain is configured to allow:
// - POST /api/v1/auth/register (public)
// - POST /api/v1/auth/token (public)
// - GET /api/v1/auth/token (public)
// - All other endpoints require JWT token in cookie

// CSRF is disabled (for REST API)
// Session policy is STATELESS (JWT-based)
```

---

### 5. Logging Configuration

```yaml
logging:
  level:
    root: INFO
    com.nishchay.identity_service: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/identity-service.log
    max-size: 10MB
    max-history: 10
```

---

### 6. Actuator Configuration (Monitoring)

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "health,info,metrics"  # Restricted in production
  
  tracing:
    export:
      zipkin:
        endpoint: http://localhost:9411/
    sampling:
      probability: 1.0  # 100% sampling (reduce in production)
  
  metrics:
    tags:
      application: identity-service
      environment: dev
```

---

### 7. Eureka Configuration (Service Discovery)

```yaml
eureka:
  client:
    # Disabled by default (see application.yaml)
    enabled: false
    
    # Enable for microservices setup
    # enabled: true
    # service-url:
    #   defaultZone: ${EUREKA_SERVER_URL:http://localhost:8761/eureka/}
  
  instance:
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
    prefer-ip-address: true
    metadata-map:
      version: ${project.version}
```

---

## Environment-Specific Profiles

### Development Profile (application-dev.yaml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/user_db
    username: root
    password: Nnn@2002
  jpa:
    hibernate:
      ddl-auto: update  # Auto-create tables
    show-sql: true

server:
  port: 9898

jwt:
  secret: dev-secret-key-not-for-production
  expiration: 1800000

logging:
  level:
    root: INFO
    com.nishchay: DEBUG

eureka:
  client:
    enabled: false
```

### Staging Profile (application-staging.yaml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://staging-db:3306/user_db
    username: app_user
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate  # Don't auto-create

server:
  port: 9898
  ssl:
    enabled: true

jwt:
  secret: ${JWT_SECRET}
  expiration: 3600000  # 1 hour

logging:
  level:
    root: INFO
    com.nishchay: INFO

eureka:
  client:
    enabled: true
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
```

### Production Profile (application-prod.yaml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db.internal:3306/user_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
  jpa:
    hibernate:
      ddl-auto: validate  # Never auto-create in production

server:
  port: 9898
  ssl:
    enabled: true
    key-store: /etc/secrets/keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}

jwt:
  secret: ${JWT_SECRET}  # Must be set via environment
  expiration: 1800000  # 30 minutes

logging:
  level:
    root: WARN
    com.nishchay: INFO
  file:
    name: /var/log/identity-service.log
    max-size: 100MB
    max-history: 30

management:
  endpoints:
    web:
      exposure:
        include: "health,info"  # Minimal exposure
  tracing:
    sampling:
      probability: 0.1  # 10% sampling in production
```

---

## Running with Profiles

```bash
# Development
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Staging
export SPRING_PROFILES_ACTIVE=staging
java -jar identity-service.jar

# Production
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET="<production-secret>"
export DB_USERNAME="<db-user>"
export DB_PASSWORD="<db-pass>"
java -jar identity-service.jar
```

---

## Docker Environment Variables

```dockerfile
FROM openjdk:21-slim

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JWT_SECRET=""  # Must be provided at runtime
ENV JWT_EXPIRATION="1800000"
ENV SPRING_DATASOURCE_URL=""
ENV SPRING_DATASOURCE_USERNAME=""
ENV SPRING_DATASOURCE_PASSWORD=""

EXPOSE 9898

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

---

## Kubernetes ConfigMap Example

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: identity-service-config
  namespace: ecommerce
data:
  application.yaml: |
    spring:
      application:
        name: identity-service
      datasource:
        url: jdbc:mysql://mysql-service:3306/user_db
        hikari:
          maximum-pool-size: 15
      jpa:
        hibernate:
          ddl-auto: validate
    
    server:
      port: 9898
      ssl:
        enabled: true
    
    jwt:
      expiration: 1800000
    
    eureka:
      client:
        enabled: true
        service-url:
          defaultZone: http://eureka-service:8761/eureka/
    
    management:
      endpoints:
        web:
          exposure:
            include: "health,info"
```

---

## Kubernetes Secret Example

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: identity-service-secrets
  namespace: ecommerce
type: Opaque
stringData:
  JWT_SECRET: "your-production-secret-key-minimum-32-chars"
  DB_PASSWORD: "secure-db-password"
  SSL_KEYSTORE_PASSWORD: "keystore-password"
```

---

## Troubleshooting

### Issue: "JWT secret not found"
**Solution:** Ensure `JWT_SECRET` environment variable is set or check `application.yaml`

```bash
# Check current value
echo $JWT_SECRET

# Set if missing
export JWT_SECRET="5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437"
```

### Issue: "Cannot connect to database"
**Solution:** Verify connection string and credentials

```bash
mysql -h localhost -u root -p user_db -e "SELECT 1;"
```

### Issue: "JWT validation fails on every request"
**Solution:** Check if JWT_SECRET matches between identity-service and api-gateway

```bash
# Both services must use the same JWT secret
echo "Identity Service Secret: $JWT_SECRET"

# Verify in API Gateway configuration too
```

### Issue: "Cookie not being set"
**Solution:** Verify response headers include Set-Cookie

```bash
curl -v -X POST http://localhost:9898/api/v1/auth/token \
  -H "Content-Type: application/json" \
  -d '{"userName":"test","password":"test"}' | grep -i set-cookie
```

---

## Performance Tuning

### Database Connection Pool
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20      # Increase for high load
      minimum-idle: 5             # Warm up connections
      idle-timeout: 300000        # 5 minutes
      connection-timeout: 20000   # 20 seconds
      max-lifetime: 1800000       # 30 minutes
```

### JWT Caching (Optional)
Consider caching user details to reduce database lookups:
```java
@Service
@CacheConfig(cacheNames = "users")
public class CustomUserDetailsService implements UserDetailsService {
    @Cacheable
    public UserDetails loadUserByUsername(String username) {
        // ...
    }
}
```

### Async Processing (Optional)
```yaml
spring:
  task:
    execution:
      pool:
        core-size: 5
        max-size: 10
      thread-name-prefix: identity-async-
```

---

**Last Updated:** March 22, 2026
**Version:** 1.0

