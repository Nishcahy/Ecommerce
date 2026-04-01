# Identity-Service Deployment Checklist

## Pre-Deployment Testing

### Unit Test Cases
- [ ] `AuthService.saveUser()` - valid registration
- [ ] `AuthService.saveUser()` - duplicate username (should fail)
- [ ] `AuthService.saveUser()` - invalid email validation
- [ ] `AuthService.generateToken()` - valid credentials
- [ ] `AuthService.generateToken()` - invalid credentials
- [ ] `JwtService.generateToken()` - token contains correct claims
- [ ] `JwtService.validateToken()` - valid token passes
- [ ] `JwtService.validateToken()` - expired token fails
- [ ] `JwtService.validateToken()` - tampered token fails
- [ ] `CustomUserDetails.build()` - creates correct authorities and permissions

### Integration Tests
- [ ] POST `/api/v1/auth/register` with valid SignUpRequest
- [ ] POST `/api/v1/auth/register` with invalid email
- [ ] POST `/api/v1/auth/register` with duplicate username
- [ ] POST `/api/v1/auth/register` with weak password (< 6 chars)
- [ ] POST `/api/v1/auth/token` with valid credentials
- [ ] POST `/api/v1/auth/token` with wrong password
- [ ] POST `/api/v1/auth/token` with non-existent user
- [ ] GET `/api/v1/auth/token?token=xxx` with valid token
- [ ] GET `/api/v1/auth/token?token=xxx` with expired token
- [ ] GET `/api/v1/auth/token?token=xxx` with invalid token
- [ ] GET `/api/v1/auth/profile` with valid JWT cookie
- [ ] GET `/api/v1/auth/profile` without JWT cookie (should return 401)

### Security Tests
- [ ] JWT cookie has `HttpOnly` flag set
- [ ] JWT cookie has `Secure` flag set (HTTPS only)
- [ ] JWT cookie has `SameSite=Strict` attribute
- [ ] Password is properly hashed (not plaintext in DB)
- [ ] Token cannot be forged (signature validation works)
- [ ] Token expiration is enforced
- [ ] SQL injection prevention (parameterized queries)
- [ ] XSS prevention in error messages

### Configuration Tests
- [ ] Default JWT secret loads from `application.yaml`
- [ ] JWT_SECRET environment variable overrides default
- [ ] JWT_EXPIRATION environment variable loads correctly
- [ ] Application starts with custom environment variables
- [ ] Logs include authentication audit trail

---

## Environment Setup

### Development Environment
```bash
# No setup needed - uses defaults from application.yaml
mvn spring-boot:run
```

### Test Environment
```bash
export JWT_SECRET="test-secret-key-minimum-32-chars"
export JWT_EXPIRATION="1800000"
mvn spring-boot:run
```

### Staging Environment
```bash
export JWT_SECRET="staging-secret-key-minimum-32-chars-unique"
export JWT_EXPIRATION="3600000"
java -jar identity-service-0.0.1-SNAPSHOT.jar
```

### Production Environment
```bash
export JWT_SECRET="prod-secret-key-minimum-32-chars-unique-secure"
export JWT_EXPIRATION="1800000"  # 30 minutes
export SPRING_PROFILES_ACTIVE="prod"
export SPRING_DATASOURCE_URL="jdbc:mysql://prod-db:3306/user_db"
export SPRING_DATASOURCE_USERNAME="***"
export SPRING_DATASOURCE_PASSWORD="***"
java -jar identity-service-0.0.1-SNAPSHOT.jar
```

---

## Docker Deployment

### Build Image
```bash
mvn clean package -DskipTests
docker build -t identity-service:1.0.0 .
```

### Docker Compose
```yaml
version: '3.8'
services:
  identity-service:
    image: identity-service:1.0.0
    environment:
      JWT_SECRET: "${JWT_SECRET}"
      JWT_EXPIRATION: "${JWT_EXPIRATION}"
      SPRING_DATASOURCE_URL: "jdbc:mysql://mysql:3306/user_db"
      SPRING_DATASOURCE_USERNAME: "root"
      SPRING_DATASOURCE_PASSWORD: "${DB_PASSWORD}"
    ports:
      - "9898:9898"
    depends_on:
      - mysql
  
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: "${DB_PASSWORD}"
      MYSQL_DATABASE: "user_db"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

---

## Database Migration Checklist

### For Existing Databases
- [ ] Backup current database
- [ ] Run migration: `ALTER TABLE user_credentilas RENAME TO user_credentials;`
- [ ] Verify table renamed successfully
- [ ] Update any stored procedures/triggers (if any)
- [ ] Update application code and deploy

### For New Databases
- [ ] Hibernate `ddl-auto: update` will create correct table: `user_credentials`
- [ ] Verify schema after first run

---

## Performance & Load Testing

### Load Test Scenarios
- [ ] Simulate 100 concurrent login attempts
- [ ] Simulate 50 concurrent registration requests
- [ ] Verify response time < 100ms for auth endpoints
- [ ] Verify JWT validation completes < 5ms
- [ ] Monitor memory usage (should be stable)
- [ ] Check database connection pool usage

### Tools
```bash
# Apache JMeter
jmeter -n -t identity_service_load_test.jmx -l results.jtl

# or Gatling
mvn gatling:test
```

---

## Monitoring & Logging Setup

### Logs to Monitor
- [ ] INFO: User registration events
- [ ] WARN: Failed authentication attempts
- [ ] ERROR: Token validation failures
- [ ] DEBUG: JWT claim details (in dev only)

### Metrics to Track
- [ ] `/actuator/metrics/http.requests.count` - request count
- [ ] `/actuator/metrics/http.requests.max` - max request time
- [ ] `/actuator/health` - application health
- [ ] Error rates on auth endpoints

### Alert Conditions
- [ ] > 10 failed login attempts from same IP in 5 minutes
- [ ] > 50 failed registrations in 1 hour
- [ ] Auth endpoint response time > 500ms
- [ ] Database connection failures

---

## Post-Deployment Verification

### Endpoint Health
- [ ] GET `http://localhost:9898/actuator/health` returns UP
- [ ] GET `http://localhost:9898/api/v1/auth/register` returns 405 (POST required)
- [ ] GET `http://localhost:9898/api/v1/auth/token` returns 405 (POST required)

### Functional Verification
1. Register new user:
```bash
curl -X POST http://localhost:9898/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "testuser",
    "email": "test@example.com",
    "password": "Test@123",
    "roles": ["CUSTOMER"]
  }'
```

2. Login:
```bash
curl -X POST http://localhost:9898/api/v1/auth/token \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "testuser",
    "password": "Test@123"
  }' \
  -v  # to see cookies
```

3. Verify cookie contains JWT:
```bash
# Check Set-Cookie header in response
# Should have: token=<jwt>; HttpOnly; Secure; SameSite=Strict; Path=/
```

4. Get profile with token:
```bash
curl -X GET http://localhost:9898/api/v1/auth/profile \
  -H "Cookie: token=<jwt-from-login>" \
  -H "Content-Type: application/json"
```

---

## Rollback Plan

### If Issues Detected
1. Stop new version: `docker stop identity-service`
2. Restore database backup (if schema changes)
3. Start previous version: `docker start identity-service:old-version`
4. Notify team
5. Investigate logs
6. Fix and re-test

### Database Rollback (if table rename caused issues)
```sql
ALTER TABLE user_credentials RENAME TO user_credentilas;
```

---

## Sign-Off

| Role | Name | Date | Signature |
|------|------|------|-----------|
| Developer | | | |
| QA Lead | | | |
| DevOps | | | |
| Architect | | | |

---

## Additional Notes

### Breaking Changes
- None in this release
- Database table name changed from `user_credentilas` to `user_credentials`
  - Automatic with Hibernate but requires manual migration for existing DBs

### Security Considerations
- JWT secret MUST be at least 32 characters
- Use HTTPS in all non-dev environments
- Store secrets in secure vault (not in git)
- Rotate JWT secret periodically
- Monitor for unauthorized access attempts

### Performance Expectations
- Average login time: 50-100ms
- Average registration time: 100-200ms
- Token validation: < 5ms
- Max concurrent users: Limited by database connections

### Known Limitations
- No refresh token implementation yet
- No 2FA support yet
- Password reset not implemented
- No rate limiting on endpoints (add in future)

---

**Document Version:** 1.0
**Created:** March 22, 2026
**Last Updated:** March 22, 2026

