# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

校园食堂智能点评与推荐系统 - A Spring Boot + Vue.js canteen review and recommendation system with user authentication, dish browsing, reviews, favorites, and personalized recommendations.

## Build Commands

### Backend (canteen-system/backend/)
```bash
mvn spring-boot:run              # Start server on port 8080
mvn test                         # Run all tests
mvn test -Dtest=ClassName        # Run single test class
mvn clean package                # Build JAR
```

### Frontend (canteen-system/frontend/)
```bash
npm install                      # Install dependencies
npm run dev                      # Start dev server on port 3001
npm run build                    # Production build
```

### Database Setup
```sql
-- Execute in MySQL before first run
source e:/毕设/canteen-system/backend/src/main/resources/db/init.sql
```

## Architecture

### Backend Structure (Spring Boot 3.2.0 + MyBatis-Plus)
```
backend/src/main/java/com/canteen/system/
├── controller/     # REST endpoints (@RestController)
├── service/impl/   # Business logic layer
├── mapper/         # MyBatis-Plus data access
├── entity/         # Database entities (@TableName)
├── dto/            # Data transfer objects (Result, PageResult, DTOs)
├── config/         # JWT filter, Web config, MyBatis-Plus config
├── annotation/     # Custom @RequireAdmin for authorization
├── aspect/         # AOP aspects for @RequireAdmin
├── util/           # JwtUtil, UserContext for auth
└── exception/      # GlobalExceptionHandler
```

### Frontend Structure (Vue 3 + Vite + Pinia)
```
frontend/src/
├── views/          # Page components (Home, Login, Dish, etc.)
├── views/admin/    # Admin panel (Dishes, Reviews, Users)
├── router/         # Vue Router with auth guards (requiresAuth, requiresAdmin)
├── stores/user.js  # Pinia store: user, token, isAdmin, login/logout
└── utils/api.js    # Axios instance with JWT interceptor
```

### Key Patterns

**API Response Format:**
```java
Result.success(data)              // { code: 200, message: "操作成功", data }
Result.error("错误信息")           // { code: 500, message: "错误信息", data: null }
PageResult.of(records, total, current, size)  // Paginated response
```

**User Authentication:**
- JWT stored in localStorage, sent via `Authorization: Bearer <token>`
- `UserContext.getCurrentUserId()` retrieves current user in backend
- `@RequireAdmin` annotation restricts endpoints to admin roles

**Frontend Auth Guard:**
```javascript
// Route meta: { requiresAuth: true, requiresAdmin: true }
router.beforeEach checks useUserStore().isLoggedIn and .isAdmin
```

**User Roles:**
- Role 0: Student (普通用户)
- Role 1: System Admin (系统管理员)
- Role 2: Canteen Admin (食堂管理员)

## Configuration

- **Backend port:** 8080
- **Frontend port:** 3001 (proxies /api and /uploads to backend)
- **Database:** MySQL 8.0, `canteen_db`
- **Redis:** localhost:6379 (optional, for caching)
- **File uploads:** `e:/毕设/canteen-system/uploads/`

## Test Accounts

| Username | Password | Role |
|----------|----------|------|
| admin | 123456 | System Admin |
| student1 | 123456 | Student |
| canteen_admin | 123456 | Canteen Admin |

## Development Notes

1. **Import order for Vue files:** Vue APIs → Router/Pinia → Element Plus → utils → styles
2. **Use `<script setup>` syntax** for all Vue components
3. **Form validation:** Use `el-form` with `rules` prop
4. **API calls:** Use the `api` instance from `utils/api.js`, not raw axios
5. **Lombok is available** - use `@Data`, `@RequiredArgsConstructor`, etc.
