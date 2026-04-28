# 校园食堂智能点评与推荐系统 - Agent 开发指南

## 项目概述

基于 Spring Boot + Vue.js 的校园食堂智能点评与推荐系统，提供用户注册登录、菜品浏览搜索、点评评分、个性化推荐、食堂数据看板等功能。

### 技术栈

- **后端**: Java 17, Spring Boot 3.2.0, MyBatis-Plus, MySQL 8.0, JWT
- **前端**: Vue 3, Vite 5, Element Plus, Pinia, Axios

### 项目结构

```
canteen-system/
├── backend/src/main/java/com/canteen/system/
│   ├── controller/   # REST 控制器
│   ├── service/      # 业务逻辑层
│   ├── mapper/       # 数据访问层
│   ├── entity/       # 实体类
│   └── dto/          # 数据传输对象
├── frontend/src/
│   ├── views/        # 页面组件
│   ├── router/       # 路由配置
│   ├── stores/       # Pinia 状态管理
│   └── utils/        # 工具函数
└── uploads/          # 上传文件目录
```

---

## 构建命令

### 前端 (frontend/)

```bash
npm install          # 安装依赖
npm run dev          # 开发启动（端口 3001）
npm run build        # 生产构建
npm run preview      # 预览构建
```

### 后端 (backend/)

```bash
mvn spring-boot:run # 启动（端口 8080）
mvn test            # 运行测试
mvn test -Dtest=XX  # 运行单个测试类
```

---

## 前端代码规范

### 导入顺序

```javascript
// 1. Vue 内置 API
import { ref, reactive } from 'vue'
// 2. Vue Router / Pinia  
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
// 3. 第三方组件库
import { ElMessage } from 'element-plus'
// 4. 项目工具
import api from '../utils/api'
// 5. 样式
import '../style.css'
```

### 命名规范

- 组件文件: PascalCase（UserLogin.vue）
- 变量/函数: camelCase（userList, handleLogin）
- CSS类: kebab-case（.login-container）

### API 调用

使用统一的 `api` 实例：

```javascript
const res = await api.get('/auth/info')
await api.post('/auth/login', loginData)
```

### 组件规范

- 使用 `<script setup>` 语法
- 使用 `el-form` + `rules` 进行表单校验

---

## 后端代码规范

### REST API 响应格式

必须使用 `Result<T>` 包装返回值：

```java
return Result.success(data);   // 成功带数据
return Result.success();       // 成功无数据
return Result.error("错误信息"); // 失败
```

### Controller 规范

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

### 命名规范

- Entity: User.java, Dish.java
- DTO: LoginDTO.java, DishDTO.java
- 方法: queryDishes, getDetailById

### 用户上下文

```java
Long userId = UserContext.getCurrentUserId();
if (userId != null) {
    // 记录用户行为
}
```

---

## 注意事项

1. **端口**: 后端 8080，前端代理 /api 和 /uploads 到后端
2. **数据库**: 首次运行需执行 init.sql 初始化数据库
3. **敏感信息**: JWT 密钥和数据库密码需在生产环境修改
4. **文件上传**: 上传文件保存在 uploads/ 目录
5. **角色权限**: 系统有三种角色（学生、食堂管理员、系统管理员）

---

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 系统管理员 |
| student1 | 123456 | 学生 |
| canteen_admin | 123456 | 食堂管理员 |