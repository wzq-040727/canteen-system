# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

校园食堂智能点评与推荐系统 - A Spring Boot + Vue.js canteen review and recommendation system with user authentication, dish browsing, reviews, favorites, and personalized recommendations.

## Git Repository Structure

`canteen-system/` 是独立 git 仓库（不是 submodule）。父目录 `E:/毕设` 也有自己的 git 仓库。

- 代码在 `canteen-system/` 子目录
- code-review-graph 等工具必须用 `repo_root=E:/毕设/canteen-system`，不能用 `E:/毕设`

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
├── config/         # JWT filter, Web config, MyBatis-Plus config, JacksonConfig
├── annotation/     # Custom @RequireAdmin for authorization
├── aspect/         # AOP aspects for @RequireAdmin
├── util/           # JwtUtil, UserContext for auth
└── exception/      # GlobalExceptionHandler
```

### Frontend Structure (Vue 3 + Vite + Pinia)
```
frontend/src/
├── views/          # Page components (Home, Login, Dish, etc.)
├── views/admin/    # Admin panel (Admin, Canteens, Dishes, Reviews, Users, Announcements)
├── components/     # Reusable components (SkeletonLoader, CardSkeleton, DishCardSkeleton)
├── router/         # Vue Router with auth guards (requiresAuth, requiresAdmin)
├── stores/user.js  # Pinia store: user, token, isAdmin, login/logout
└── utils/
    ├── api.js      # Axios instance with JWT interceptor
    └── helpers.js  # getImageUrl, formatTime, defaultImage
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
- Role 1: Canteen Admin (食堂管理员)
- Role 2: System Admin (系统管理员)

**Key Entities (extended):**
- `Canteen` — 新增 `floorCount` (楼层数)；`status` 支持动态判断（0=手动关闭，1=根据 `openingHours` 自动判断当前是否营业中）
- `Window` — 新增 `floor` (所在楼层), `openTime`/`closeTime` (营业时间)
- `User` — 新增 `securityQuestion`/`securityAnswer` (忘记密码安全问题)
- `Dish` — 新增 `floor` 临时字段（来自 Window 表），菜品来源显示格式：`来自XX食堂X楼XX窗口`
- `Announcement` — 食堂公告，关联 `canteenId`，支持置顶和有效期

**Key API Endpoints (new):**
- `GET /api/windows/canteen/{id}/floors` — 获取食堂楼层列表
- `GET /api/windows/canteen/{id}?floor=N` — 按楼层筛选窗口
- `POST/PUT/DELETE /api/windows` — 窗口 CRUD (@RequireAdmin)
- `GET /api/auth/security-question?username=` — 获取安全问题
- `POST /api/auth/reset-password` — 重置密码
- `POST /api/dishes/import` — Excel 批量导入菜品 (@RequireAdmin)
- `GET /api/reviews/recent-grouped` — 按食堂-楼层-窗口分组的评价
- `GET/POST/PUT/DELETE /api/announcements` — 公告管理
- `GET /api/announcements` — 获取全部有效公告（跨食堂）
- `GET /api/dishes/categories` — 获取菜品分类及数量统计

## Configuration

- **Backend port:** 8080
- **Frontend port:** 3001 (proxies /api and /uploads to backend)
- **Database:** MySQL 8.0, `canteen_db`
- **File uploads:** `e:/毕设/canteen-system/uploads/`

## Test Accounts

| Username | Password | Role |
|----------|----------|------|
| admin | 123456 | System Admin |
| student1 | 123456 | Student |
| canteen_admin | 123456 | Canteen Admin |

## Development Notes

### Frontend Conventions

**Import order:** Vue APIs → Router/Pinia → Element Plus → utils → styles

**Naming:**
- 组件文件: PascalCase（`UserLogin.vue`）
- 变量/函数: camelCase（`userList`, `handleLogin`）
- CSS 类: kebab-case（`.login-container`）

**Rules:**
- Use `<script setup>` syntax for all Vue components
- Form validation: use `el-form` with `rules` prop
- API calls: use the `api` instance from `utils/api.js`, not raw axios

### Backend Conventions

**Controller pattern:**
```java
@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {
    private final DishService dishService;

    @GetMapping
    public Result<PageResult<Dish>> query(DishQueryDTO queryDTO) {
        return Result.success(dishService.queryDishes(queryDTO));
    }

    @PostMapping
    @RequireAdmin(message = "需要管理员权限")
    public Result<Void> addDish(@RequestBody DishDTO dishDTO) {
        dishService.addDish(dishDTO);
        return Result.success();
    }
}
```

**Naming:** Entity: `User.java`, DTO: `LoginDTO.java`, 方法: `queryDishes`, `getDetailById`

**UserContext:**
```java
Long userId = UserContext.getCurrentUserId();
```

**Lombok:** available — use `@Data`, `@RequiredArgsConstructor`, etc.
