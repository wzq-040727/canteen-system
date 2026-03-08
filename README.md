# 校园食堂智能点评与推荐系统

## 项目简介

本系统是一个基于 Spring Boot + Vue.js 的校园食堂智能点评与推荐系统，实现了用户注册登录、菜品浏览与搜索、点评评分、个性化推荐、食堂数据看板等功能。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- MyBatis-Plus 3.5.5
- MySQL 8.0
- JWT 认证

### 前端
- Vue 3
- Vite 5
- Element Plus
- Pinia
- Axios

## 项目结构

```
canteen-system/
├── backend/                    # 后端项目
│   ├── src/main/java/
│   │   └── com/canteen/system/
│   │       ├── controller/     # 控制器层
│   │       ├── service/        # 服务层
│   │       ├── mapper/         # 数据访问层
│   │       ├── entity/         # 实体类
│   │       ├── dto/            # 数据传输对象
│   │       ├── config/         # 配置类
│   │       ├── util/           # 工具类
│   │       └── exception/      # 异常处理
│   └── src/main/resources/
│       ├── application.yml     # 配置文件
|        |
│       └── db/init.sql         # 数据库初始化脚本
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # 状态管理
│   │   └── utils/              # 工具函数
│   └── package.json
└── uploads/                    # 上传文件目录
```

## 快速开始

### 1. 环境准备

确保已安装以下软件：
- JDK 17+
- MySQL 8.0+
- Node.js 18+
- Maven 3.6+

### 2. 数据库配置

1. 创建数据库并导入初始数据：
```sql
-- 在 MySQL 中执行
source e:/毕设/canteen-system/backend/src/main/resources/db/init.sql
```

或者手动在 Navicat 中执行 init.sql 文件。

2. 修改数据库配置（如需要）：
编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/canteen_db?...
    username: root
    password: root  # 修改为你的密码
```

### 3. 启动后端

```bash
cd e:/毕设/canteen-system/backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动。

### 4. 启动前端

```bash
cd e:/毕设/canteen-system/frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3002 启动。

### 5. 访问系统

打开浏览器访问 http://localhost:3002

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 系统管理员 |
| student1 | 123456 | 学生 |
| canteen_admin | 123456 | 食堂管理员 |

> 注意：初始密码需要在数据库中设置 BCrypt 加密后的值。首次使用请先注册新用户。

## 功能模块

### 用户端
- 用户注册与登录
- 食堂浏览
- 菜品搜索与筛选
- 菜品详情查看
- 评价与评分
- 收藏菜品
- 个人中心
- 个性化推荐

### 管理端
- 数据看板
- 菜品管理
- 评价管理
- 用户管理

## API 接口

### 认证相关
- POST /api/auth/login - 登录
- POST /api/auth/register - 注册
- GET /api/auth/info - 获取当前用户信息

### 菜品相关
- GET /api/dishes - 菜品列表
- GET /api/dishes/{id} - 菜品详情
- GET /api/dishes/top - 热门菜品
- GET /api/dishes/recommend - 个性化推荐

### 评价相关
- POST /api/reviews - 提交评价
- GET /api/reviews/dish/{dishId} - 获取菜品评价

### 其他
- GET /api/canteens - 食堂列表
- GET /api/windows/canteen/{canteenId} - 窗口列表
- GET /api/dashboard - 数据看板

## 开发说明

### 推荐算法
系统采用协同过滤算法实现个性化推荐：
1. 记录用户行为（浏览、收藏、评分、评论）
2. 基于用户行为相似度计算推荐分数
3. 为用户生成个性化菜品推荐列表

### 文件上传
用户平价上传的图片保存在 `uploads/` 目录下，通过 `/uploads/{filename}` 访问。

## 注意事项

1. 确保 MySQL 服务已启动
2. 确保后端服务先于前端启动
3. 首次运行需要初始化数据库
4. 生产环境请修改 JWT 密钥和数据库密码
