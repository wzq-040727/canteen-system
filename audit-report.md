# 项目审查报告

**项目名称：** 校园食堂智能点评与推荐系统（canteen-system）
**审查时间：** 2026-06-06
**审查范围：** 全栈（Spring Boot + Vue）

## 总览

| 维度 | CRITICAL | HIGH | MEDIUM | LOW | 合计 |
|------|----------|------|--------|-----|------|
| ① 控制器审查 | 2 | 8 | 9 | 6 | 25 |
| ② 前端组件审查 | 0 | 5 | 8 | 6 | 19 |
| ③ 配置与安全审查 | 2 | 5 | 7 | 4 | 18 |
| ④ 服务层审查 | 3 | 7 | 6 | 4 | 20 |
| ⑤ 前端工具审查 | 2 | 3 | 4 | 3 | 12 |
| **合计** | **9** | **28** | **34** | **23** | **94** |

## 优先修复建议（Top 10）

1. ❌ [CRITICAL] AdminPermissionAspect 切点不覆盖类级别 @RequireAdmin，ReviewAdminController/UserController 可能无权限保护 — `AdminPermissionAspect.java:17`
2. ❌ [CRITICAL] AuthController.updateUserInfo 直接接收 User 实体，可篡改 role/password 实现权限提升 — `AuthController.java:34`
3. ❌ [CRITICAL] deleteDish 未检查关联评价/收藏数据，直接软删除导致孤儿数据 — `DishServiceImpl.java:193`
4. ❌ [CRITICAL] importDishes 无 @Transactional，部分成功导致数据不一致 — `DishServiceImpl.java:223`
5. ❌ [CRITICAL] deleteWindow 存在并发竞态条件 — `WindowServiceImpl.java:83`
6. ❌ [CRITICAL] 数据库密码和 JWT 密钥硬编码在 application.yml — `application.yml:6,33`
7. ⚠️ [HIGH] 8 处 @RequestBody 缺少 @Valid 注解，任意输入不做校验 — 多个 Controller
8. ⚠️ [HIGH] CORS 允许所有来源 + JwtFilter 对无效 Token 静默放行 — `WebConfig.java`, `JwtFilter.java`
9. ⚠️ [HIGH] 多个 Service 方法缺少 @Transactional（addFavorite、deleteReview、register 等）— 多个 ServiceImpl
10. ⚠️ [HIGH] DashboardServiceImpl 全表加载计算平均分，性能隐患 — `DashboardServiceImpl.java:78`

---

# ① 控制器审查报告

## 摘要

| 指标 | 数值 |
|------|------|
| 审查控制器数 | 12 |
| CRITICAL | 2 |
| HIGH | 8 |
| MEDIUM | 9 |
| LOW | 6 |

## AuthController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/AuthController.java`

### [HIGH]：updateUserInfo 直接接收实体且缺少 @Valid
- 位置：第 34 行
- 说明：`@RequestBody User user` 直接绑定数据库实体，没有 `@Valid` 校验。攻击者可以提交任意字段（如 `role=2` 将自己提升为系统管理员），造成**权限提升漏洞**。
- 建议：使用专用 DTO（如 `UpdateUserInfoDTO`），仅暴露允许修改的字段（realName, phone, email, avatar），加上 `@Valid`。

### [HIGH]：updatePassword 通过 @RequestParam 传递密码
- 位置：第 40 行
- 说明：密码以 URL 查询参数传递，会被记录在服务器日志、浏览器历史中。
- 建议：改用 `@RequestBody` + DTO，通过 POST body 传递密码。

### [MEDIUM]：getCurrentUser 返回 User 实体
- 位置：第 29 行
- 说明：返回完整实体会将 `password`、`securityAnswer` 序列化到 JSON 响应。
- 建议：返回专用 VO，或在实体上用 `@JsonIgnore` 标注敏感字段。

### [MEDIUM]：updateUserInfo 无身份校验
- 位置：第 34 行
- 说明：任何已登录用户可修改任意用户信息（如果 id 被篡改）。
- 建议：忽略请求体中的 id，始终使用 `UserContext.getCurrentUserId()`。

## AnnouncementController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/AnnouncementController.java`

### [HIGH]：add/update 接收实体且无 @Valid
- 位置：第 30、37 行
- 说明：缺少对 title/content 的非空校验，客户端可设置 id、createdTime 等内部字段。
- 建议：使用 `AnnouncementDTO` + `@Valid`。

### [MEDIUM]：返回列表无分页
- 位置：第 19、24 行
- 说明：公告数量增长后可能导致响应过大。

## CanteenController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/CanteenController.java`

### [HIGH]：缺少 Create 和 Delete 接口
- 说明：只有 Read 和 Update，管理员无法通过 API 新增或删除食堂。

### [HIGH]：updateForManage 接收 DTO 无 @Valid
- 位置：第 38 行
- 建议：添加校验注解和 `@Valid`。

## DashboardController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/DashboardController.java`

### [HIGH]：Dashboard 端点无权限控制
- 位置：第 17、22 行
- 说明：统计数据属于管理敏感信息，不应向普通用户暴露。
- 建议：添加 `@RequireAdmin`。

## DishController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/DishController.java`

### [HIGH]：addDish/updateDish 接收 DTO 无 @Valid
- 位置：第 66、73 行
- 说明：可提交空 name、负数 price。updateDish 使用 PUT 到 `/api/dishes`（无 id 路径），不够 RESTful。

### [MEDIUM]：getDetail 中记录用户行为的逻辑不应在 Controller 层
- 位置：第 30-37 行
- 建议：移入 Service 层。

### [MEDIUM]：getCategoryStats 返回原始 Map
- 位置：第 60 行
- 建议：定义 `CategoryStatsVO`。

## FavoriteController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/FavoriteController.java`

### [MEDIUM]：getUserFavorites 存在 N+1 查询风险
- 位置：第 57-68 行
- 建议：使用 `dishService.getByIds(dishIds)` 批量查询。

### [LOW]：使用 HashMap 构造返回值
- 位置：第 47 行
- 建议：`Result<Boolean>` 替代。

## MyReviewController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/MyReviewController.java`

### [HIGH]：Controller 直接注入 Mapper 编写业务查询
- 位置：第 19、27-31 行
- 建议：封装到 Service 层。

### [HIGH]：返回 Entity 而非 DTO
- 位置：第 22 行
- 说明：`deleted` 等内部字段会暴露给前端。

### [MEDIUM]：缺少分页
- 位置：第 22 行

## ReviewAdminController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/ReviewAdminController.java`

### [HIGH]：Controller 直接注入 Mapper 编写业务查询
- 位置：第 19、29-36 行

### [HIGH]：返回 Entity 而非 DTO
- 位置：第 22 行

### [MEDIUM]：硬编码占位数据
- 位置：第 32-33 行
- 说明：`"用户"+id` 和 `"菜品"+id` 是临时占位，并非真实数据。

## ReviewController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/ReviewController.java`

### [HIGH]：deleteReview 无权限控制
- 位置：第 66 行
- 说明：任何用户可删除任意评价。
- 建议：添加 `@RequireAdmin` 或校验作者归属。

### [MEDIUM]：auditReview 的 status 参数无校验
- 位置：第 73 行

## UploadController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/UploadController.java`

### [MEDIUM]：使用 @Value 字段注入而非构造器注入
- 位置：第 15 行

### [LOW]：异常处理使用 e.printStackTrace()
- 位置：第 72 行
- 建议：使用 SLF4J Logger。

### [LOW]：路径拼接方式不安全
- 位置：第 60 行

## UserController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/UserController.java`

### [HIGH]：Controller 直接注入 Mapper
- 位置：第 20 行

### [HIGH]：updateStatus 无输入校验
- 位置：第 35 行
- 说明：status 可传入任意值，未校验用户是否存在。

### [HIGH]：返回 Entity 而非 DTO
- 位置：第 23 行
- 说明：暴露 `password`、`securityAnswer` 等敏感字段。

## WindowController

**文件路径：** `backend/src/main/java/com/canteen/system/controller/WindowController.java`

### [HIGH]：addWindow/updateWindow 接收实体且无 @Valid
- 位置：第 34、41 行

## 跨控制器一致性问题

### [CRITICAL]：类级别 @RequireAdmin 在 Aspect 中可能不生效
- 位置：`AdminPermissionAspect.java` 第 17 行
- 说明：Aspect 切点 `@annotation(...)` 只匹配方法级别，类级别需要 `@within(...)`。ReviewAdminController 和 UserController 的所有端点可能没有权限保护。
- 建议：切点改为 `@annotation(...) || @within(...)`。

### [CRITICAL]：AuthController.updateUserInfo 权限提升
- 说明：可篡改 role=2 提升为管理员。

### [MEDIUM]：3 个 Controller 直接注入 Mapper 绕过 Service 层
- 涉及：MyReviewController、ReviewAdminController、UserController

### [MEDIUM]：8 处 @RequestBody 缺少 @Valid 注解

### [MEDIUM]：8 个 Controller 直接返回 Entity，泄露内部字段

---

# ② 前端组件审查报告

## 摘要

| 指标 | 数值 |
|------|------|
| 审查页面组件数 | 15 |
| 审查公共组件数 | 3 |
| 路由配置数 | 15 |
| CRITICAL | 0 |
| HIGH | 5 |
| MEDIUM | 8 |
| LOW | 6 |

## 路由分析

所有 15 条路由引用的组件文件均存在，无白屏风险。路由守卫配置合理：`/profile`、`/favorites`、`/my-reviews` 设置了 `requiresAuth`；`/admin` 下 6 个路由均设置了 `requiresAuth` + `requiresAdmin`。

### [LOW]：缺少 404 页面路由
- 路由配置中没有 catch-all `/:pathMatch(.*)` 路由。

## 页面组件

### Dish.vue
#### [HIGH]：fetchDish、fetchReviews、fetchSmartReview、checkFavorite 均无 try-catch
- 位置：第 123-142 行
- 说明：API 调用失败时页面直接报错且无降级处理。

#### [MEDIUM]：模板中直接调用 JSON.parse
- 位置：第 54 行
- 说明：`review.images` 格式异常会导致渲染崩溃。

### Canteen.vue
#### [HIGH]：fetchCanteen 无 try-catch
- 位置：第 92-95 行

#### [MEDIUM]：重复定义 defaultImage 和 getImageUrl
- 位置：第 77 行
- 说明：未复用 `utils/helpers.js`。

### Favorites.vue
#### [HIGH]：fetchFavorites 无 try-catch
- 位置：第 41-43 行

#### [MEDIUM]：重复定义 defaultImage 和 getImageUrl
- 位置：第 33-39 行

### Profile.vue
#### [HIGH]：updateInfo 和 updatePassword 均无 try-catch
- 位置：第 109-131 行

#### [MEDIUM]：修改密码缺少 el-form rules 验证
- 位置：第 32-46 行

#### [MEDIUM]：头像展示但无上传功能
- 位置：第 7 行

### Login.vue
#### [MEDIUM]：登录失败时 catch 块无额外用户反馈
- 位置：第 87 行

#### [MEDIUM]：查询安全问题失败时无用户可见错误
- 位置：第 132 行

### Register.vue
#### [MEDIUM]：缺少 `<style scoped>` 块
- 说明：复用 Login.vue 的样式类，可能依赖全局 CSS。

### Search.vue
#### [MEDIUM]：loading 初始值为 false，首次加载会短暂显示空状态
- 位置：第 73 行

### admin/Admin.vue
#### [MEDIUM]：dashboard API 调用无 try-catch 和 loading 状态
- 位置：第 93-95 行

### admin/Users.vue
#### [MEDIUM]：缺少用户搜索/筛选功能

### admin/Canteens.vue
#### [MEDIUM]：updateStatus 和 updateHours 无 try-catch
- 位置：第 160-166 行

### admin/Announcements.vue
#### [MEDIUM]：N+1 循环逐个食堂获取公告
- 位置：第 99-117 行
- 建议：使用 `/api/announcements` 全量接口。

### MyReviews.vue
#### [MEDIUM]：重复定义 formatTime
- 位置：第 33-36 行

### LOW 级别问题
- DishCardSkeleton.vue 空 `<script setup>` 可删除
- Search.vue + admin/Dishes.vue 分类选项硬编码
- Login.vue resetDialogState 重置方式不一致
- admin/Canteens.vue console.error 残留
- MyReviews.vue 缺少编辑/删除评价功能
- 缺少 404 路由

---

# ③ 配置与数据库审查报告

## 摘要

| 指标 | 数值 |
|------|------|
| CRITICAL | 2 |
| HIGH | 5 |
| MEDIUM | 7 |
| LOW | 4 |

## 配置文件

### [CRITICAL]：数据库密码硬编码
- 文件：`application.yml` 第 6 行
- 说明：`password: 123456` 且使用 root 账户。
- 建议：使用 `${DB_PASSWORD}`，创建专用数据库用户。

### [CRITICAL]：JWT 密钥硬编码且强度不足
- 文件：`application.yml` 第 33 行
- 说明：`secret: canteen-system-jwt-secret-key-for-graduation-project-2024` 可预测。
- 建议：`openssl rand -base64 64` 生成高熵密钥，使用 `${JWT_SECRET}`。

### [HIGH]：CORS 配置过于宽松
- 文件：`WebConfig.java` 第 12-19 行
- 说明：`allowedOriginPatterns("*")` + `allowCredentials(true)` 允许任意来源携带 Cookie。
- 建议：限制为实际前端域名。

### [HIGH]：JwtFilter 对无效令牌静默放行
- 文件：`JwtFilter.java` 第 24-33 行
- 说明：Token 无效时不返回 401，继续执行 `chain.doFilter`。
- 建议：对无效 Token 返回 401。

### [HIGH]：useSSL=false 禁用数据库连接加密
- 文件：`application.yml` 第 4 行

### [MEDIUM]：日志配置打印全部 SQL
- 文件：`application.yml` 第 20 行
- 建议：生产环境使用 Slf4jImpl。

### [MEDIUM]：Redis 无密码保护
- 文件：`application.yml` 第 8-11 行

### [MEDIUM]：文件上传路径硬编码 Windows 绝对路径
- 文件：`application.yml` 第 37 行

### [MEDIUM]：缺少安全 HTTP 响应头
- 说明：缺失 X-Content-Type-Options、X-Frame-Options、CSP。

### [MEDIUM]：GlobalExceptionHandler 泄露异常堆栈
- 文件：`GlobalExceptionHandler.java` 第 26-27 行
- 说明：`e.printStackTrace()` + RuntimeException 直接返回 `e.getMessage()`。

### [MEDIUM]：文件资源映射硬编码路径
- 文件：`WebConfig.java` 第 23-26 行

### [MEDIUM]：Jackson 全局禁用 FAIL_ON_UNKNOWN_PROPERTIES
- 文件：`JacksonConfig.java` 第 29 行

### [LOW]：未配置 HikariCP 连接池参数
### [LOW]：文件上传未配置类型限制
### [LOW]：JwtFilter 未注册 URL 模式

## 数据库脚本

### [HIGH]：所有测试用户使用相同 BCrypt 哈希（密码均为 123456）
- 文件：`init.sql` 第 164-202 行

### [HIGH]：recommendation 和 review_like 表缺少索引
- 建议：`ALTER TABLE recommendation ADD KEY idx_dish_id (dish_id);`
- 建议：`ALTER TABLE review_like ADD KEY idx_review_id (review_id);`

### [HIGH]：未设置外键约束（FOREIGN KEY）
- 说明：数据完整性完全依赖应用层。

### [HIGH]：security_answer 明文存储
- 建议：使用 BCrypt 哈希。

### [MEDIUM]：user_behavior 表缺少逻辑删除字段
### [MEDIUM]：canteen 表 name 缺少唯一约束
### [MEDIUM]：window 表缺少 (canteen_id, name) 唯一约束
### [MEDIUM]：recommendation 表缺少 (user_id, dish_id) 唯一约束
### [LOW]：缺少 CHECK 约束（rating 1-5, price >0）
### [LOW]：recommendation 表缺少逻辑删除字段
### [LOW]：索引命名前缀不统一

---

# ④ 服务层审查报告

## 摘要

| 指标 | 数值 |
|------|------|
| 审查 Service 数 | 9 |
| CRITICAL | 3 |
| HIGH | 7 |
| MEDIUM | 6 |
| LOW | 4 |

## 异常处理

### [HIGH]：全局异常处理器未区分业务异常与系统异常
- 文件：`GlobalExceptionHandler.java` 第 11-14 行
- 说明：所有 RuntimeException 返回 code=200，前端无法区分业务校验失败和系统故障。
- 建议：定义 `BusinessException`，系统异常返回 500。

### [HIGH]：全局异常处理器使用 e.printStackTrace()
- 文件：`GlobalExceptionHandler.java` 第 26 行

### [MEDIUM]：缺少对 HttpMessageNotReadableException 等的处理

## DishServiceImpl

### [CRITICAL]：deleteDish 未检查关联评价和收藏数据
- 位置：第 193-195 行
- 建议：删除前检查关联数据，或级联处理。

### [CRITICAL]：importDishes 无 @Transactional
- 位置：第 223-355 行
- 说明：逐条 save 无事务保护，部分成功导致数据不一致。

### [HIGH]：addDish/updateDish 未校验 canteenId 和 windowId 是否存在
- 位置：第 174-189 行

### [HIGH]：直接注入 Mapper 而非 Service
- 位置：第 34-37 行

### [MEDIUM]：getSmartReview 方法过长（~50 行）

## WindowServiceImpl

### [CRITICAL]：deleteWindow 存在并发竞态条件
- 位置：第 83-89 行
- 建议：加 `@Transactional` 或使用外键约束。

### [HIGH]：updateWindow 未校验楼层合法性
- 位置：第 74-80 行

## ReviewServiceImpl

### [HIGH]：deleteReview 无权限校验
- 位置：第 134-139 行

### [HIGH]：deleteReview 缺少 @Transactional
- 位置：第 134-139 行

### [MEDIUM]：auditReview 缺少 @Transactional
### [MEDIUM]：getDishReviews 中 N+1 查询问题

## UserServiceImpl

### [HIGH]：register 无 @Transactional
- 位置：第 49-80 行

### [HIGH]：resetPassword 安全答案明文比较
- 位置：第 169 行

### [MEDIUM]：所有异常统一使用裸 RuntimeException（15 处）
### [MEDIUM]：getCurrentUser 返回实体对象含密码字段

## FavoriteServiceImpl

### [HIGH]：addFavorite 缺少 @Transactional
- 位置：第 29-39 行

### [MEDIUM]：removeFavorite 无校验是否已收藏

## AnnouncementServiceImpl

### [HIGH]：addAnnouncement 未校验 canteenId 是否存在
- 位置：第 57-59 行

## DashboardServiceImpl

### [HIGH]：calculateOverallAvgRating 加载全表数据到内存
- 位置：第 78-88 行
- 建议：使用 SQL `AVG()` 聚合。

### [HIGH]：getRatingDistribution 执行 5 次独立查询
- 位置：第 66-76 行
- 建议：合并为 `SELECT rating, COUNT(*) GROUP BY rating`。

## CanteenServiceImpl

### [LOW]：applyEffectiveStatus 空 catch 块
- 位置：第 64-66 行

## UserBehaviorServiceImpl

### [MEDIUM]：Mapper 有 selectUserPreferences/selectCollaborativeFiltering 但 Service 层未封装

---

# ⑤ 前端工具与状态管理审查报告

## 摘要

| 指标 | 数值 |
|------|------|
| 审查工具函数文件数 | 2 |
| 审查 Store 数 | 1 |
| CRITICAL | 2 |
| HIGH | 3 |
| MEDIUM | 4 |
| LOW | 3 |

## API 实例 (utils/api.js)

### [HIGH]：401 响应时未清除 Pinia Store 状态
- 位置：第 28-30 行
- 说明：只清除了 localStorage，未清除 store 中的 user/token。
- 建议：导入并调用 `useUserStore().logout()`。

### [MEDIUM]：IMAGE_BASE 硬编码 localhost:8080
- 位置：第 9 行
- 建议：改用环境变量或相对路径 `/uploads/xxx`。

### [MEDIUM]：响应拦截器丢弃 HTTP 状态码信息
- 位置：第 21-24 行

### [LOW]：全局超时 10000ms 不可配置

## 工具函数 (utils/helpers.js)

### [HIGH]：MyReviews.vue 重复定义 formatTime
- 位置：`MyReviews.vue` 第 33-37 行

### [HIGH]：admin/Dishes.vue 重复定义 getImageUrl 且行为不一致
- 位置：`Dishes.vue` 第 169-173 行

### [MEDIUM]：formatTime 不处理无效日期
- 位置：`helpers.js` 第 11-15 行
- 说明：`Invalid Date` 的 `getFullYear()` 返回 NaN，UI 显示 "NaN-NaN-NaN"。

## 状态管理 (stores/user.js)

### [CRITICAL]：token 未与 store 状态跨标签页同步
- 位置：第 7 行
- 说明：另一标签页登出后当前标签页 store 残留旧登录态。
- 建议：监听 `storage` 事件。

### [CRITICAL]：login action 中 user.value = res.data 将含 token 的整个响应作为 user 对象
- 位置：第 13-17 行
- 建议：显式解构 `const { token: t, ...userInfo } = res.data`。

### [MEDIUM]：fetchUserInfo 网络错误也会触发 logout
- 位置：第 31-39 行
- 建议：仅在 401 时 logout。

## API 调用覆盖

### 后端有但前端未调用的接口
- `GET /api/dashboard/canteen/{canteenId}` — 按食堂查看仪表盘
- `POST /api/upload` — 未通过 axios 实例调用，不经过请求拦截器

### 前端调用但后端不存在的接口
- 无

## 测试与文档

### [HIGH]：前端零测试文件
### [LOW]：缺少前端 README
### [LOW]：缺少 .env 配置文件
