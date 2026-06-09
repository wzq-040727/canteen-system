# 🍽️ 校园食堂智能点评与推荐系统

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?logo=springboot&logoColor=white)
![Vue](https://img.shields.io/badge/Vue-3.4-4FC08D?logo=vuedotjs&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue)

**基于 Spring Boot + Vue 3 的高校食堂智能点评与协同过滤推荐系统**

[功能概览](#-功能模块) • [快速开始](#-快速开始) • [系统架构](#-系统架构) • [API 文档](#-api-接口) • [贡献指南](#-贡献指南)

</div>

---

## 📖 项目简介

校园食堂智能点评与推荐系统是一套面向高校校园场景的综合性 Web 应用，旨在通过互联网技术构建学生与食堂之间的信息桥梁。系统采用前后端分离架构，集成**基于用户的协同过滤推荐算法**，实现了菜品信息浏览、点评评分互动、个性化推荐及管理端数据看板等核心功能。

> 🎓 本项目为 **安徽工业大学 2026 届软件工程专业本科毕业设计**，作者：王志强，指导教师：王喜凤教授。

### 解决的痛点

| 痛点 | 解决方案 |
|------|----------|
| 🚫 信息不对称，不知道今天食堂有什么菜 | 菜品信息实时展示，多条件搜索筛选 |
| 💬 缺乏反馈渠道，意见无法传达到管理层 | 星级评分 + 文字点评 + 图片上传 |
| 😵 选择困难，不知道吃什么好 | 协同过滤个性化推荐，智能匹配口味 |
| 📊 食堂管理者缺乏数据决策依据 | 可视化数据看板，多维度统计分析 |

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                       前端 (Vue 3 + Vite)                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐  │
│  │  Home.vue │ │ Dish.vue │ │ Login.vue│ │ Admin/*.vue   │  │
│  └──────────┘ └──────────┘ └──────────┘ └───────────────┘  │
│  ┌──────────────────────────────────────────────────────┐   │
│  │   Pinia (状态管理)  │  Vue Router (路由守卫)          │   │
│  │   Axios (HTTP 请求)  │  Element Plus (UI 组件库)      │   │
│  └──────────────────────────────────────────────────────┘   │
└───────────────────────┬─────────────────────────────────────┘
                        │  HTTP / RESTful API
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                      后端 (Spring Boot 3.2)                   │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────────┐ │
│  │  Controller  │ │    AOP       │ │  Exception Handler   │ │
│  │  (REST API)  │ │ (@RequireAdmin)│ │  (Global Exception)  │ │
│  └──────────────┘ └──────────────┘ └──────────────────────┘ │
│  ┌──────────────────────────────────────────────────────┐   │
│  │          Service Layer (业务逻辑 + 推荐算法)           │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │       MyBatis-Plus (ORM)  +  JWT 认证过滤器           │   │
│  └──────────────────────────────────────────────────────┘   │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
                   ┌──────────┐
                   │ MySQL 8  │
                   └──────────┘
```

---

## 🛠️ 技术栈

### 后端
| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.2.0 | 核心框架 |
| MyBatis-Plus | 3.5.5 | ORM 持久层 |
| MySQL | 8.0 | 关系型数据库 |
| JWT (jjwt) | 0.12.3 | 身份认证 |
| Spring AOP | — | 权限切面控制 |
| Lombok | — | 代码简化 |
| Hutool | 5.8.24 | 工具类库 |

### 前端
| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4 | 前端框架 |
| Vite | 5.0 | 构建工具 |
| Element Plus | 2.4 | UI 组件库 |
| Pinia | 2.1 | 状态管理 |
| Vue Router | 4.2 | 路由管理 |
| Axios | 1.6 | HTTP 客户端 |

---

## 🚀 快速开始

### 环境要求

在开始之前，请确保你的开发环境满足以下要求：

| 软件 | 最低版本 | 说明 |
|------|----------|------|
| JDK | 17+ | [下载地址](https://adoptium.net/) |
| Maven | 3.6+ | [下载地址](https://maven.apache.org/) |
| MySQL | 8.0+ | [下载地址](https://dev.mysql.com/downloads/) |
| Node.js | 18+ | [下载地址](https://nodejs.org/) |
| Git | 2.0+ | [下载地址](https://git-scm.com/) |

### 1. 克隆项目

```bash
git clone https://github.com/yourusername/canteen-system.git
cd canteen-system
```

### 2. 初始化数据库

打开 MySQL 客户端，执行初始化脚本：

```sql
-- 方式一：命令行
mysql -u root -p < backend/src/main/resources/db/init.sql

-- 方式二：MySQL 客户端中
source /path/to/canteen-system/backend/src/main/resources/db/init.sql;

-- 方式三：使用 Navicat / DataGrip 等工具直接打开 init.sql 执行
```

初始化脚本将自动完成以下操作：
- 创建 `canteen_db` 数据库（utf8mb4 编码）
- 创建 8 张核心数据表（user, canteen, window, dish, review, user_behavior, favorite, review_like）
- 插入测试数据（3 个食堂、10+ 窗口、20+ 菜品）
- 创建预置测试账号

### 3. 配置数据库连接

编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/canteen_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root        # 修改为你的 MySQL 用户名
    password: 123456      # 修改为你的 MySQL 密码
```

> ⚠️ **生产环境安全提示**：请修改 `jwt.secret` 为随机长字符串，切勿使用默认值！

### 4. 启动后端服务

```bash
cd backend

# 编译并启动
mvn spring-boot:run

# 或者先打包再运行
mvn clean package -DskipTests
java -jar target/canteen-system-1.0.0.jar
```

后端服务启动成功后，控制台将显示：

```
Tomcat started on port(s): 8080 (http)
Started CanteenSystemApplication in X.XXX seconds
```

### 5. 启动前端服务

打开新的终端窗口：

```bash
cd frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

前端开发服务器将在 `http://localhost:3001` 启动，API 请求自动代理至后端 8080 端口。

### 6. 访问系统

打开浏览器访问 **http://localhost:3001** 即可使用系统。

---

## 👥 测试账号

系统初始化脚本预置了三种角色的测试账号，密码统一为 `123456`：

| 用户名 | 密码 | 角色 | 权限范围 |
|--------|------|------|----------|
| `student1` | `123456` | 学生 | 浏览菜品、发表点评、收藏、查看推荐 |
| `canteen_admin` | `123456` | 食堂管理员 | 除学生权限外，可管理菜品和窗口信息 |
| `admin` | `123456` | 系统管理员 | 全部权限，含用户管理、评论审核、数据看板 |

> 💡 **提示**：所有密码通过 BCrypt 加密存储，也可通过注册页面创建新账号。

---

## ✨ 功能模块

### 🎓 学生端

| 功能 | 说明 |
|------|------|
| 🔐 注册/登录 | 支持用户名注册、JWT 认证登录，自动保持登录状态 |
| 🏫 食堂浏览 | 查看校园各食堂的基本信息、位置、营业时间 |
| 🔍 菜品搜索 | 按名称关键词搜索、按食堂/分类/口味多条件组合筛选 |
| 📋 菜品详情 | 查看菜品完整信息、用户评论列表（分页）、相关推荐 |
| ⭐ 点评评分 | 1-5 星量化评分 + 文字评论 + 图片上传 |
| ❤️ 收藏功能 | 收藏感兴趣的菜品，收藏行为同步反馈推荐算法 |
| 👍 评论互动 | 对他人评论进行点赞/取消点赞 |
| 🤖 智能推荐 | 首页展示基于协同过滤算法的个性化菜品推荐列表 |
| 👤 个人中心 | 查看/修改个人资料、查看历史评论记录 |

### 🔧 管理端

| 功能 | 说明 |
|------|------|
| 📊 数据看板 | 概览统计（总用户/菜品/评论数、平均评分）、评分分布图、热门菜品排行 |
| 🍜 菜品管理 | 菜品的增删改查（逻辑删除）、上下架管理、推荐标记 |
| 💬 评论审核 | 评论列表查看、隐藏/显示审核、删除管理 |
| 👥 用户管理 | 用户列表查看、角色分配、账号启用/禁用 |

### 🧠 推荐算法

系统核心采用 **基于用户的协同过滤（User-based CF）** 算法：

```
用户行为采集 → 行为权重计算 → SQL 子查询相似用户 → Top-N 推荐
      ↓                ↓          ↓              ↓
  浏览(1分)         邻居用户     排除已交互菜品     个性化推荐列表
  收藏(3分)         相似行为     按分数排序        冷启动兜底：
  评分(4分)                                     热门高评分菜品
  评论(5分)
```

---

## 📡 API 接口

> 所有接口返回统一格式：`{ "code": 200, "message": "操作成功", "data": {...} }`

### 认证接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `POST` | `/api/auth/register` | 用户注册 | 无需 |
| `POST` | `/api/auth/login` | 用户登录，返回 JWT | 无需 |
| `GET` | `/api/auth/info` | 获取当前登录用户信息 | Bearer Token |

### 菜品接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `GET` | `/api/dishes` | 菜品列表（支持筛选/排序/分页） | 无需 |
| `GET` | `/api/dishes/{id}` | 菜品详情（含窗口/食堂信息） | 无需 |
| `GET` | `/api/dishes/top` | 热门菜品 Top-N | 无需 |
| `GET` | `/api/dishes/recommend` | 个性化推荐列表 | Bearer Token |

#### 菜品列表查询示例

```http
GET /api/dishes?canteenId=1&category=热菜&keyword=鸡&page=1&size=10
```

**支持的查询参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `canteenId` | long | 食堂 ID |
| `windowId` | long | 窗口 ID |
| `category` | string | 菜品分类 |
| `taste` | string | 口味标签 |
| `keyword` | string | 菜品名称关键词 |
| `page` | int | 页码（默认 1） |
| `size` | int | 每页条数（默认 10） |

### 评论接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `POST` | `/api/reviews` | 发表评论（评分+文字+图片） | Bearer Token |
| `GET` | `/api/reviews/dish/{dishId}` | 获取菜品评论（分页） | 无需 |
| `POST` | `/api/reviews/{id}/like` | 评论点赞/取消点赞 | Bearer Token |

#### 发表评论示例

```http
POST /api/reviews
Content-Type: multipart/form-data
Authorization: Bearer <token>

dishId=1&rating=4&content=味道不错，分量也足！
```

### 收藏接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `POST` | `/api/favorites` | 收藏/取消收藏菜品 | Bearer Token |
| `GET` | `/api/favorites` | 我的收藏列表 | Bearer Token |

### 食堂/窗口接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `GET` | `/api/canteens` | 食堂列表 | 无需 |
| `GET` | `/api/windows/canteen/{canteenId}` | 食堂下窗口列表 | 无需 |

### 管理端接口（需管理员权限）

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/dashboard` | 数据看板统计 |
| `POST/PUT/DELETE` | `/api/admin/dishes` | 菜品 CRUD |
| `GET/PUT` | `/api/admin/reviews` | 评论审核管理 |
| `GET/PUT` | `/api/admin/users` | 用户管理 |

---

## 📁 项目结构

```
canteen-system/
├── backend/                              # 后端 Spring Boot 项目
│   ├── pom.xml                           # Maven 依赖配置
│   └── src/main/
│       ├── java/com/canteen/system/
│       │   ├── CanteenSystemApplication.java    # 启动入口
│       │   ├── controller/                      # REST 控制器层
│       │   │   ├── AuthController.java          #   认证（注册/登录）
│       │   │   ├── DishController.java          #   菜品（搜索/详情/推荐）
│       │   │   ├── ReviewController.java        #   评论（发表/点赞/查询）
│       │   │   ├── CanteenController.java       #   食堂/窗口
│       │   │   ├── FavoriteController.java      #   收藏
│       │   │   └── admin/                       #   管理端控制器
│       │   ├── service/                         # 业务逻辑层
│       │   │   └── impl/                        #   接口实现
│       │   ├── mapper/                          # MyBatis-Plus 数据访问
│       │   ├── entity/                          # 数据库实体（@TableName）
│       │   ├── dto/                             # 数据传输对象
│       │   │   ├── LoginDTO.java                #   登录请求体
│       │   │   ├── PageResult.java              #   分页响应
│       │   │   └── Result.java                  #   统一响应封装
│       │   ├── config/                          # 配置类
│       │   │   ├── JwtFilter.java               #   JWT 认证过滤器
│       │   │   ├── WebConfig.java               #   跨域配置
│       │   │   └── MyBatisPlusConfig.java       #   分页插件配置
│       │   ├── annotation/                      # 自定义注解
│       │   │   └── RequireAdmin.java            #   管理员权限注解
│       │   ├── aspect/                          # AOP 切面
│       │   │   └── AdminPermissionAspect.java   #   权限校验切面
│       │   ├── util/                            # 工具类
│       │   │   ├── JwtUtil.java                 #   JWT 生成/解析
│       │   │   └── UserContext.java             #   ThreadLocal 用户上下文
│       │   └── exception/                       # 全局异常处理
│       └── resources/
│           ├── application.yml                  # 配置文件
│           └── db/
│               └── init.sql                     # 数据库初始化脚本
│
├── frontend/                              # 前端 Vue 3 项目
│   ├── package.json                       # NPM 依赖配置
│   ├── vite.config.js                     # Vite 配置（含代理）
│   └── src/
│       ├── App.vue                        # 根组件
│       ├── main.js                        # 入口文件
│       ├── views/                         # 页面组件
│       │   ├── Home.vue                   #   首页（推荐 + 公告）
│       │   ├── Login.vue                  #   登录页
│       │   ├── Register.vue               #   注册页
│       │   ├── Canteen.vue                #   食堂/菜品浏览
│       │   ├── Dish.vue                   #   菜品详情（评论+推荐）
│       │   ├── Search.vue                 #   菜品搜索
│       │   ├── Favorites.vue              #   我的收藏
│       │   ├── Profile.vue                #   个人中心
│       │   ├── MyReviews.vue              #   我的评论
│       │   └── admin/                     #   管理端页面
│       │       ├── Admin.vue              #     数据看板
│       │       ├── Dishes.vue             #     菜品管理
│       │       ├── Reviews.vue            #     评论审核
│       │       └── Users.vue              #     用户管理
│       ├── components/                    # 通用组件
│       │   ├── SkeletonLoader.vue         #   骨架屏加载器
│       │   ├── CardSkeleton.vue           #   卡片骨架屏
│       │   └── DishCardSkeleton.vue       #   菜品卡片骨架屏
│       ├── router/
│       │   └── index.js                   # 路由配置 + 权限守卫
│       ├── stores/
│       │   └── user.js                    # Pinia 用户状态管理
│       └── utils/
│           ├── api.js                     # Axios 实例 + 拦截器
│           └── helpers.js                 # 工具函数
│
└── uploads/                               # 用户上传图片目录
```

---

## ⚙️ 配置说明

### 后端配置 (`application.yml`)

```yaml
# 数据库连接
spring.datasource:
  url: jdbc:mysql://localhost:3306/canteen_db?...    # 数据库地址
  username: root                                      # 数据库用户名
  password: 123456                                    # 数据库密码

# 文件上传
spring.servlet.multipart:
  max-file-size: 10MB                                 # 单文件最大 10MB
  max-request-size: 10MB                              # 请求体最大 10MB

# JWT 配置
jwt:
  secret: <your-random-secret-key>                    # 签名密钥（生产环境需修改）
  expiration: 86400000                                # 令牌有效期（24小时，毫秒）

# 文件存储
file:
  upload-path: e:/毕设/canteen-system/uploads/        # 上传文件保存路径

# MyBatis-Plus 配置
mybatis-plus.configuration:
  map-underscore-to-camel-case: true                  # 驼峰命名转换
  log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL 日志

# 服务端口
server.port: 8080
```

### 前端代理配置 (`vite.config.js`)

开发模式下，Vite 自动将 `/api` 和 `/uploads` 请求代理到后端：

```javascript
server: {
  port: 3001,
  proxy: {
    '/api': { target: 'http://localhost:8080', changeOrigin: true },
    '/uploads': { target: 'http://localhost:8080', changeOrigin: true }
  }
}
```

---

## 🔐 安全特性

- **密码加密**：用户密码通过 BCrypt 哈希算法加密存储，永不保存明文
- **JWT 认证**：无状态的 Token 认证机制，24 小时自动过期
- **角色权限**：三级角色体系（学生/食堂管理员/系统管理员），通过自定义注解 `@RequireAdmin` + AOP 切面实现接口级权限控制
- **SQL 注入防护**：全程使用 MyBatis-Plus LambdaQueryWrapper 构建参数化查询
- **逻辑删除**：所有数据表采用逻辑删除（`deleted` 字段），保护数据完整性
- **前端路由守卫**：`router.beforeEach` 校验认证状态和角色权限，拦截未授权访问

---

## 🧪 测试

### 功能测试

系统已完成 19 个功能测试用例，覆盖所有核心模块：

| 模块 | 测试用例数 | 通过率 |
|------|-----------|--------|
| 用户管理 | 6 | 100% |
| 点评模块 | 5 | 100% |
| 推荐模块 | 3 | 100% |
| 菜品管理 | 5 | 100% |

### 性能测试

使用 JMeter 在模拟并发场景下的测试结果：

| 测试场景 | 并发数 | 平均响应时间 | TPS | 错误率 |
|---------|--------|-------------|-----|--------|
| 菜品列表查询 | 100 | 85ms | 156.3 | 0% |
| 评论发表 | 50 | 128ms | 45.2 | 0% |
| 推荐列表查询 | 100 | 152ms | 98.7 | 0% |

---

## 🤝 贡献指南

我们欢迎任何形式的贡献！无论是报告 Bug、提出新功能建议、改进文档，还是提交代码。

### 贡献流程

1. **Fork 本仓库** 到你的 GitHub 账号
2. **创建功能分支**：
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **进行开发**，请遵循以下规范：

<details>
<summary><b>代码规范</b></summary>

#### Java 后端
- 使用 `@RequiredArgsConstructor` 依赖注入，而非 `@Autowired`
- Controller 保持简洁，业务逻辑下沉至 Service 层
- 所有 API 返回使用 `Result` / `PageResult` 统一封装
- 实体类使用 Lombok 注解（`@Data`、`@NoArgsConstructor`）
- 命名规范：
  - Entity：`User.java`（与表名对应）
  - DTO：`LoginDTO.java`、`DishQueryDTO.java`
  - Service 方法：`queryDishes`、`getDetailById`

#### Vue 前端
- 使用 `<script setup>` 语法糖编写组件
- 组件文件使用 PascalCase（如 `UserLogin.vue`）
- CSS 类使用 kebab-case（如 `.login-container`）
- API 调用统一使用 `utils/api.js` 中的 Axios 实例
- 导入顺序：Vue API → Router/Pinia → Element Plus → 工具 → 样式

</details>

<details>
<summary><b>Commit 规范</b></summary>

使用 [Conventional Commits](https://www.conventionalcommits.org/) 格式：

```bash
feat: 添加菜品标签筛选功能
fix: 修复评论重复提交的并发问题
docs: 更新 API 接口文档
refactor: 重构推荐算法 SQL 查询
test: 添加评论模块单元测试
chore: 升级 Spring Boot 至 3.2.1
```

允许的类型：`feat` | `fix` | `docs` | `refactor` | `test` | `chore` | `style` | `perf`

</details>

4. **编写测试**（如有新增逻辑，请补充测试用例）
5. **提交代码**：
   ```bash
   git add .
   git commit -m "feat: 功能描述"
   ```
6. **推送分支**：
   ```bash
   git push origin feature/your-feature-name
   ```
7. **创建 Pull Request** 到主仓库的 `main` 分支
8. **等待 Code Review**，根据反馈修改

### 分支命名

| 分支类型 | 命名格式 | 示例 |
|---------|---------|------|
| 功能 | `feature/描述` | `feature/dish-tag-filter` |
| 修复 | `fix/描述` | `fix/review-duplicate-submit` |
| 重构 | `refactor/描述` | `refactor/recommend-sql` |

---

## 🔧 常见问题

<details>
<summary><b>Q: 启动后端报 "Communications link failure"</b></summary>

MySQL 服务未启动或连接信息有误。请确认：
1. MySQL 服务是否正在运行（`net start mysql` 或服务管理器中检查）
2. `application.yml` 中的数据库地址、用户名、密码是否正确
3. 数据库 `canteen_db` 是否已创建（执行 `init.sql`）

</details>

<details>
<summary><b>Q: 前端页面空白/接口 404</b></summary>

1. 确认后端服务已启动且运行在 8080 端口
2. 确认前端启动在 3001 端口
3. 检查 Vite 代理配置是否与后端端口一致
4. 打开浏览器开发者工具 → Network 面板查看具体错误

</details>

<details>
<summary><b>Q: 登录后提示 Token 无效或过期</b></summary>

1. 检查后端 `jwt.secret` 是否被修改（前后端密钥不一致）
2. Token 默认 24 小时过期，重新登录即可
3. 清除浏览器 localStorage 中的 token 后重新登录

</details>

<details>
<summary><b>Q: 图片上传失败</b></summary>

1. 检查 `uploads/` 目录是否存在且有写入权限
2. 确认上传文件大小未超过 10MB 限制
3. 检查 `file.upload-path` 配置的路径是否正确

</details>

<details>
<summary><b>Q: 推荐列表为空</b></summary>

1. 新用户需要先浏览、收藏或评论一些菜品，积累行为数据后才会产生个性化推荐
2. 无行为数据时会自动展示热门菜品（冷启动策略）
3. 检查 `user_behavior` 表中是否有对应的行为记录

</details>

---

## 🗺️ 路线图

- [x] 用户注册登录（JWT 认证）
- [x] 食堂/窗口/菜品三级信息展示
- [x] 菜品搜索与多条件筛选
- [x] 星级评分 + 文字评论 + 图片上传
- [x] 评论点赞互动
- [x] 菜品收藏
- [x] 基于用户协同过滤的个性化推荐
- [x] 冷启动热门推荐兜底
- [x] 管理端数据看板
- [x] 菜品/评论/用户管理
- [x] 骨架屏加载优化
- [ ] Redis 缓存层集成
- [ ] 微信小程序端
- [ ] 混合推荐算法（Content-based + CF）
- [ ] 基于 NLP 的评论情感分析
- [ ] Docker 容器化部署
- [ ] 单元测试覆盖率提升至 80%+

---

## 📄 许可证

本项目基于 [MIT License](LICENSE) 开源。

---

## 🙏 致谢

- **指导教师**：王喜凤教授，在选题立意、系统设计和论文撰写过程中给予了悉心指导
- **安徽工业大学计算机科学与技术学院**：提供了良好的学习和研究环境
- **开源社区**：感谢 Spring Boot、Vue.js、Element Plus、MyBatis-Plus 等优秀开源项目

---

<div align="center">
  <sub>Built with ❤️ by 王志强 | 安徽工业大学 2026 届毕业设计</sub>
</div>
