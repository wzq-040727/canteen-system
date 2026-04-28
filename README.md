项目概述
校园食堂智能点评与推荐系统——Spring Boot + Vue.js食堂评测与推荐系统，支持用户认证、浏览菜品、评测、收藏夹和个性化推荐。

构建命令
后端（食堂系统/后端/）
mvn spring-boot:run              # Start server on port 8080
mvn test                         # Run all tests
mvn test -Dtest=ClassName        # Run single test class
mvn clean package                # Build JAR
前端（食堂系统/前端/）
npm install                      # Install dependencies
npm run dev                      # Start dev server on port 3001
npm run build                    # Production build
数据库设置
-- Execute in MySQL before first run
source e:/毕设/canteen-system/backend/src/main/resources/db/init.sql
建筑
后端结构（Spring Boot 3.2.0 + MyBatis-Plus）
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
前端结构（Vue 3 + Vite + Pinia）
frontend/src/
├── views/          # Page components (Home, Login, Dish, etc.)
├── views/admin/    # Admin panel (Dishes, Reviews, Users)
├── router/         # Vue Router with auth guards (requiresAuth, requiresAdmin)
├── stores/user.js  # Pinia store: user, token, isAdmin, login/logout
└── utils/api.js    # Axios instance with JWT interceptor
关键模式
API响应格式：

Result.success(data)              // { code: 200, message: "操作成功", data }
Result.error("错误信息")           // { code: 500, message: "错误信息", data: null }
PageResult.of(records, total, current, size)  // Paginated response
用户认证：

JWT 存储在 localStorage，发送方式为Authorization: Bearer <token>
UserContext.getCurrentUserId()在后端检索当前用户
@RequireAdmin注释限制端点只能担任管理员角色
前端认证守卫：

// Route meta: { requiresAuth: true, requiresAdmin: true }
router.beforeEach checks useUserStore().isLoggedIn and .isAdmin
用户角色：

角色0：普通用户
角色1：系统管理员（系统管理员）
职位2：食堂管理员（食堂管理员）
配置
后端端口：8080
前端端口：3001（代理/api和/uploads到后端）
数据库：MySQL 8.0，canteen_db
Redis：localhost：6379（可选，用于缓存）
文件上传： e:/毕设/canteen-system/uploads/
测试账号
用户名	密码	职责
行政	123456	系统管理员
学生1	123456	学生
canteen_admin	123456	食堂管理
开发笔记
Vue文件导入顺序：Vue API → Router/Pinia → Element Plus → 利用 → 样式
所有Vue组件都使用<script setup>语法
形式验证：与螺旋桨的使用el-formrules
API调用：使用实例中的 ，而不是原始的公理apiutils/api.js
龙目岛有——使用，等等。@Data@RequiredArgsConstructor
