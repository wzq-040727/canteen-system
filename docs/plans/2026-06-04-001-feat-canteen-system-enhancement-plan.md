# feat: 校园食堂系统功能增强计划

**状态:** completed (2026-06-04)
**创建日期:** 2026-06-04
**目标:** 让系统更贴合实际校园食堂场景，增加楼层分类、忘记密码、批量导入、公告等核心功能

---

## 问题背景

当前系统存在以下不足：
- 食堂详情页只有窗口列表，没有楼层维度，不符合多层食堂的实际结构
- 登录页没有忘记密码功能，用户体验不完整
- 首页评价平铺展示，没有按食堂/楼层/窗口分类
- 管理员添加菜品只能逐条操作，没有批量导入和图片上传
- 食堂管理功能过于简单，缺少楼层和窗口的 CRUD
- 没有公告功能，管理员无法发布临时通知

---

## 需求追溯

| 编号 | 需求 | 来源 |
|------|------|------|
| R1 | 登录页新增忘记密码功能 | 用户需求 #1 |
| R2 | 食堂页面新增楼层分类，楼层下显示窗口，窗口下显示菜品图片 | 用户需求 #2 |
| R3 | 首页评价按食堂-楼层-窗口分类展示 | 用户需求 #3 |
| R4 | 管理员支持批量导入菜品（Excel）、添加菜品图片、食堂-楼层-窗口分类 | 用户需求 #4 |
| R5 | 食堂管理按楼层分类，支持窗口增删改，每层最多10个窗口 | 用户需求 #5 |
| R6 | 窗口营业时间，非营业时段显示暂停提示 | 补充需求 |
| R7 | 食堂公告功能 | 补充需求（让系统更贴合实际） |

---

## 关键技术决策

### KTD1: 忘记密码采用安全问题验证

**决策:** 用户注册时设置安全问题和答案，忘记密码时回答问题即可重置。

**理由:** 毕设项目无需依赖外部邮件/SMS服务，安全问题方案零外部依赖、实现简单、功能完整。

**影响:** User 表新增 `security_question` 和 `security_answer` 字段；注册流程增加安全问题设置；新增 `/api/auth/reset-password` 接口。

### KTD2: 楼层数据模型 — Canteen.floorCount + Window.floor

**决策:** Canteen 表新增 `floor_count`（总楼层数），Window 表新增 `floor`（所属楼层号）。

**理由:** 两个字段各司其职：`floor_count` 管理员设置食堂有几层楼，`floor` 标记每个窗口在几楼。查询"食堂A的2楼窗口"需要 Window.floor；校验"窗口楼层不能超过食堂总层数"需要 Canteen.floorCount。

**影响:** 两表各新增一个字段；新增按楼层查询窗口的 API；添加/编辑窗口时做楼层校验。

### KTD3: 批量导入采用 Apache POI 处理 Excel

**决策:** 使用 Apache POI 库解析 .xlsx 文件，逐行校验并导入。

**理由:** POI 是 Java 生态最成熟的 Excel 处理库，Spring Boot 项目集成方便；xlsx 格式比 CSV 更通用，支持单元格格式和数据校验。

**模板列定义:** `菜品名称 | 食堂名称 | 楼层 | 窗口名称 | 价格 | 分类 | 口味 | 描述`

**影响:** 后端 pom.xml 新增 poi-ooxml 依赖；新增 `/api/dishes/import` 接口（multipart/form-data）；前端新增导入按钮和文件上传组件。

### KTD4: 窗口营业时间存储格式

**决策:** Window 表新增 `open_time` (TIME) 和 `close_time` (TIME) 字段。

**理由:** 独立的开/关时间比拼接字符串更利于查询和比较。前端展示时拼接为 "06:00-09:00" 格式。

### KTD5: 食堂公告独立实体

**决策:** 新建 `announcement` 表，关联 canteen_id，支持标题、内容、置顶、有效期。

**理由:** 公告是独立于食堂描述的临时性信息，需要独立管理生命周期（发布/过期/删除）。

---

## 实施计划

### U1. 数据库 Schema 变更

**目标:** 所有表结构变更一次性完成，为后续后端开发奠定基础

**文件:**
- `backend/src/main/resources/db/alter_tables.sql`（新建）
- `backend/src/main/resources/db/init.sql`（同步更新表结构）

**方案:**
```sql
-- User 表：忘记密码
ALTER TABLE user ADD COLUMN security_question VARCHAR(200) COMMENT '安全问题';
ALTER TABLE user ADD COLUMN security_answer VARCHAR(200) COMMENT '安全问题答案';

-- Canteen 表：楼层总数
ALTER TABLE canteen ADD COLUMN floor_count INT DEFAULT 1 COMMENT '楼层数';

-- Window 表：楼层 + 营业时间
ALTER TABLE window ADD COLUMN floor INT DEFAULT 1 COMMENT '所在楼层';
ALTER TABLE window ADD COLUMN open_time TIME COMMENT '开始营业时间';
ALTER TABLE window ADD COLUMN close_time TIME COMMENT '结束营业时间';

-- Dish 表：排序字段（已有 sortOrder，确认存在）

-- 新建 announcement 表
CREATE TABLE announcement (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  canteen_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  content TEXT,
  is_top TINYINT DEFAULT 0 COMMENT '是否置顶',
  start_time DATETIME COMMENT '生效开始时间',
  end_time DATETIME COMMENT '生效结束时间',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_canteen_id (canteen_id)
);
```

**测试场景:**
- 执行 ALTER 语句后，各表新字段存在且类型正确
- announcement 表可正常 CRUD
- 现有数据不受影响（新字段均有默认值或允许 NULL）

**验证:** 执行 SQL 后 `DESC user/canteen/window/announcement` 确认字段存在。

---

### U2. 忘记密码 — 后端

**目标:** 实现安全问题验证 + 密码重置的完整后端逻辑

**依赖:** U1

**文件:**
- `backend/src/main/java/com/canteen/system/entity/User.java`（修改：新增字段）
- `backend/src/main/java/com/canteen/system/dto/ResetPasswordDTO.java`（新建）
- `backend/src/main/java/com/canteen/system/controller/AuthController.java`（修改：新增2个端点）
- `backend/src/main/java/com/canteen/system/service/UserService.java`（修改：新增方法）
- `backend/src/main/java/com/canteen/system/service/impl/UserServiceImpl.java`（修改：实现）

**方案:**

新增2个 API 端点：
1. `GET /api/auth/security-question?username={username}` — 根据用户名查询安全问题（返回问题，不暴露答案）
2. `POST /api/auth/reset-password` — 提交用户名、安全问题答案、新密码，验证通过后重置

重置流程：
1. 根据 username 查找用户
2. 验证 security_answer（BCrypt.checkpw 或明文比较）
3. BCrypt.hashpw 新密码并更新
4. 注册时同步要求填写安全问题（修改 RegisterDTO）

**测试场景:**
- 已设置安全问题的用户，查询安全问题返回正确内容
- 不存在的用户名，查询返回提示信息
- 正确回答问题 + 合法新密码 → 密码重置成功，可用新密码登录
- 错误答案 → 返回错误提示，密码不变
- 新密码长度不足6位 → 校验失败

**验证:** 用新密码调用 `/api/auth/login` 登录成功。

---

### U3. 忘记密码 — 前端

**目标:** 登录页增加忘记密码入口，注册页增加安全问题设置

**依赖:** U2

**文件:**
- `frontend/src/views/Login.vue`（修改）
- `frontend/src/views/Register.vue`（修改）

**方案:**

Login.vue 新增：
- 登录表单下方增加"忘记密码？"链接
- 点击弹出 el-dialog，步骤式流程：
  1. 输入用户名 → 调用 GET 接口获取安全问题
  2. 显示安全问题，用户输入答案 + 新密码 + 确认密码
  3. 提交重置 → 成功后关闭弹窗，提示重新登录

Register.vue 新增：
- 注册表单底部增加安全问题和答案输入框（必填）
- 提交时一并传给后端

**测试场景:**
- 登录页点击"忘记密码"弹出对话框
- 输入已注册用户名，正确显示安全问题
- 输入未注册用户名，提示用户不存在
- 正确填写答案和新密码，重置成功后可用新密码登录
- 注册时必须填写安全问题，否则提交失败

**验证:** 完整走通"忘记密码 → 重置 → 新密码登录"流程。

---

### U4. 楼层管理 — 后端

**目标:** Canteen/Window 实体新增楼层字段，支持按楼层查询窗口，窗口 CRUD 完善

**依赖:** U1

**文件:**
- `backend/src/main/java/com/canteen/system/entity/Canteen.java`（修改）
- `backend/src/main/java/com/canteen/system/entity/Window.java`（修改）
- `backend/src/main/java/com/canteen/system/dto/WindowDTO.java`（新建）
- `backend/src/main/java/com/canteen/system/controller/WindowController.java`（修改：扩展端点）
- `backend/src/main/java/com/canteen/system/service/WindowService.java`（修改）
- `backend/src/main/java/com/canteen/system/service/impl/WindowServiceImpl.java`（修改/新建）
- `backend/src/main/java/com/canteen/system/mapper/WindowMapper.java`（修改）

**方案:**

新增/修改 API 端点：
1. `GET /api/windows/canteen/{canteenId}` — 已有，修改为支持按楼层筛选（可选参数 `floor`）
2. `GET /api/windows/canteen/{canteenId}/floors` — 新增，返回该食堂所有楼层列表（去重）
3. `POST /api/windows` — 新增窗口（@RequireAdmin），校验同层窗口数 ≤ 10
4. `PUT /api/windows/{id}` — 编辑窗口（@RequireAdmin）
5. `DELETE /api/windows/{id}` — 删除窗口（@RequireAdmin），同时检查该窗口下是否有菜品

窗口新增逻辑：
- 创建窗口时检查：该食堂该楼层已有窗口数 < 10，否则返回"该楼层窗口数已达上限（10个）"
- 删除窗口时检查：若窗口下有关联菜品，提示"请先移除该窗口下的菜品"

CanteenController 修改：
- `PUT /api/canteen/admin/update` 支持更新 `floorCount`

**测试场景:**
- 获取食堂楼层列表返回 [1, 2, 3]
- 按楼层筛选窗口：`floor=2` 只返回2楼的窗口
- 新增窗口到已有9个窗口的楼层 → 成功（变为10个）
- 新增窗口到已有10个窗口的楼层 → 失败，提示上限
- 删除有菜品的窗口 → 失败，提示先移除菜品
- 删除无菜品的窗口 → 成功

**验证:** 通过 API 完成"查询楼层 → 查询楼层窗口 → 新增窗口 → 编辑窗口 → 删除窗口"完整流程。

---

### U5. 食堂详情页改造 — 楼层分类展示

**目标:** Canteen.vue 改为按楼层分组展示窗口，窗口下显示菜品图片

**依赖:** U4

**文件:**
- `frontend/src/views/Canteen.vue`（重写）

**方案:**

页面结构改为：
```
食堂信息区（名称、位置、营业时间、描述）
├── 楼层选择区（el-tabs 或 el-radio-group，按楼层切换）
│   ├── 1楼
│   │   ├── 窗口A卡片（窗口名称 + 营业状态 + 菜品缩略图网格）
│   │   │   ├── 菜品图片1（点击跳转详情）
│   │   │   ├── 菜品图片2
│   │   │   └── ...
│   │   └── 窗口B卡片
│   ├── 2楼
│   │   └── ...
│   └── 3楼
│       └── ...
```

数据加载流程：
1. `GET /canteen/{id}` 获取食堂信息（含 floorCount）
2. `GET /windows/canteen/{id}/floors` 获取楼层列表
3. 切换楼层时 `GET /windows/canteen/{id}?floor=N` 获取该层窗口
4. 每个窗口下 `GET /dishes?windowId=X&status=1&pageSize=6` 获取菜品（只显示图片+名称+价格）

窗口卡片显示：
- 窗口名称、菜系类型标签
- 营业时间（如有 open_time/close_time，非营业时段显示灰色"已暂停营业"）
- 菜品图片网格（最多显示6张，点击"查看更多"展开）

**测试场景:**
- 1楼食堂只显示1个楼层 tab，3楼食堂显示3个 tab
- 切换楼层 tab，窗口列表正确更新
- 窗口下菜品图片正确加载，点击跳转菜品详情
- 窗口非营业时段显示"已暂停营业，营业时间为 06:00-09:00"
- 无菜品的窗口显示"暂无菜品"占位

**验证:** 浏览器访问食堂详情页，楼层切换、窗口展示、菜品图片均正常。

---

### U6. 首页评价分组展示

**目标:** 首页最新评价按 食堂-楼层-窗口 分组展示

**依赖:** U4

**文件:**
- `backend/src/main/java/com/canteen/system/controller/ReviewController.java`（修改）
- `backend/src/main/java/com/canteen/system/service/ReviewService.java`（修改）
- `backend/src/main/java/com/canteen/system/service/impl/ReviewServiceImpl.java`（修改）
- `backend/src/main/java/com/canteen/system/entity/Review.java`（修改：新增关联字段）
- `frontend/src/views/Home.vue`（修改）

**方案:**

后端：
- Review 查询联表获取 dish → window → canteen 信息
- 新增 `GET /api/reviews/recent-grouped?limit=N` 接口，返回按食堂分组的评价数据
- 返回结构：`[{ canteenName, floors: [{ floor, windows: [{ windowName, reviews: [...] }] }] }]`

前端 Home.vue 评价区域改为：
```
最新评价
├── 食堂A
│   ├── 1楼 - 窗口1: 评价1, 评价2
│   ├── 2楼 - 窗口3: 评价3
│   └── ...
├── 食堂B
│   └── ...
```
- 使用 el-collapse 或分组卡片展示
- 每条评价保持原有样式（用户头像、评分星级、内容、时间）

**测试场景:**
- 评价按食堂分组正确，食堂内按楼层排序
- 无评价的食堂/楼层/窗口不显示空分组
- 评价内容截断展示（超过2行折叠，点击展开）
- 数据为空时显示"暂无评价"

**验证:** 首页评价区域按层级结构展示，视觉上清晰对应实际食堂布局。

---

### U7. 管理员菜品管理增强 — 批量导入与图片上传

**目标:** 管理员支持 Excel 批量导入菜品、添加菜品图片、按食堂-楼层-窗口分类选择

**依赖:** U4

**文件:**
- `backend/pom.xml`（修改：新增 poi-ooxml 依赖）
- `backend/src/main/java/com/canteen/system/controller/DishController.java`（修改）
- `backend/src/main/java/com/canteen/system/service/DishService.java`（修改）
- `backend/src/main/java/com/canteen/system/service/impl/DishServiceImpl.java`（修改）
- `backend/src/main/java/com/canteen/system/dto/DishDTO.java`（修改）
- `frontend/src/views/admin/Dishes.vue`（修改）

**方案:**

**后端 — 批量导入：**
- 新增 `POST /api/dishes/import`（@RequireAdmin，multipart/form-data）
- 使用 POI 解析 xlsx，模板列：`菜品名称 | 食堂名称 | 楼层 | 窗口名称 | 价格 | 分类 | 口味 | 描述`
- 逐行校验：
  - 食堂名称必须存在于数据库（模糊匹配）
  - 楼层必须在食堂 floorCount 范围内
  - 窗口名称必须存在于该食堂该楼层
  - 价格必须 > 0
  - 分类必须在允许值范围内
- 校验失败的行记录错误原因，全部处理后返回导入结果（成功N条，失败N条+失败详情）
- 导入成功时默认 status=1（上架）

**后端 — 菜品图片：**
- 现有 `POST /api/upload` 已支持图片上传
- DishDTO 的 image 字段传入上传后的 URL 即可
- 无需新增后端接口，前端组装即可

**前端 — Dishes.vue 改造：**
- 添加/编辑对话框增加图片上传组件（el-upload，调用 `/api/upload`）
- 所属食堂/窗口选择改为三级联动：食堂 → 楼层 → 窗口
- 新增"批量导入"按钮：
  - 点击弹出 el-dialog
  - 提供"下载模板"链接
  - el-upload 上传 xlsx 文件
  - 上传后显示导入结果（成功/失败条数+失败原因表格）

**测试场景:**
- 下载模板文件，填入正确数据，导入成功，菜品列表新增对应记录
- 模板中食堂名称不存在 → 该行导入失败，提示"食堂不存在"
- 模板中楼层超出范围 → 该行导入失败，提示"楼层无效"
- 模板中价格为负数 → 该行导入失败
- 上传空文件 → 提示"文件为空"
- 上传非 xlsx 文件 → 提示"文件格式不正确"
- 菜品添加时上传图片，图片正确显示在菜品卡片上
- 三级联动：选食堂后楼层下拉更新，选楼层后窗口下拉更新

**验证:** 完整走通"下载模板 → 填写 → 导入 → 查看导入结果 → 在菜品列表中确认"流程。

---

### U8. 食堂管理增强 — 楼层与窗口管理

**目标:** 管理后台食堂管理页面按楼层管理窗口，支持窗口增删改

**依赖:** U4

**文件:**
- `frontend/src/views/admin/Canteens.vue`（重写）

**方案:**

页面结构改为：
```
食堂列表表格（名称、位置、楼层数、营业状态、营业时间、操作）
├── 操作列：编辑楼层/窗口 按钮
│   点击弹出 el-drawer 或 el-dialog：
│   ├── 楼层 tab 列表（1楼、2楼、3楼...）
│   │   ├── 该层窗口列表表格（窗口名、菜系、营业时间、状态、操作）
│   │   │   ├── 编辑按钮 → 弹窗修改窗口信息
│   │   │   └── 删除按钮 → 确认后删除
│   │   └── 底部"新增窗口"按钮（该层 < 10 个时可用，否则 disabled + tooltip 提示）
│   └── 食堂基础信息编辑（名称、位置、描述、楼层数、营业时间）
```

编辑食堂信息时：
- 修改楼层数时提示"减少楼层将影响该楼层的窗口数据，请确认"
- 楼层数不能小于当前已有窗口的最大楼层值

**测试场景:**
- 点击"编辑楼层/窗口"打开侧边栏，楼层 tab 正确显示
- 切换楼层 tab，窗口列表更新
- 新增窗口：填写名称、菜系、营业时间 → 成功
- 新增第11个窗口 → 按钮禁用，tooltip 显示"该楼层窗口数已达上限（10个）"
- 编辑窗口名称 → 保存成功
- 删除无菜品的窗口 → 成功
- 删除有菜品的窗口 → 失败提示
- 食堂楼层数从3改为2 → 提示确认

**验证:** 管理员完整操作食堂的楼层和窗口管理功能。

---

### U9. 窗口营业时间功能

**目标:** 窗口支持设置营业时间，非营业时段前端显示暂停提示

**依赖:** U4

**文件:**
- `backend/src/main/java/com/canteen/system/entity/Window.java`（已在 U4 修改）
- `backend/src/main/java/com/canteen/system/vo/WindowVO.java`（新建，包含 isOpen 计算）
- `backend/src/main/java/com/canteen/system/service/impl/WindowServiceImpl.java`（修改）
- `frontend/src/views/Canteen.vue`（已在 U5 修改）
- `frontend/src/views/admin/Canteens.vue`（已在 U8 修改）

**方案:**

后端：
- Window 返回数据时计算 `isOpen` 字段：当前时间在 open_time 和 close_time 之间则为 true
- 若 open_time/close_time 为 NULL，视为全天营业（isOpen=true）

前端 Canteen.vue：
- 窗口卡片上显示营业状态标签：营业中（绿色）/ 已暂停营业（灰色）
- 暂停状态显示："已暂停营业，营业时间为 HH:MM-HH:MM"
- 窗口卡片整体降低透明度

前端 admin/Canteens.vue：
- 新增/编辑窗口时增加 el-time-picker 选择营业时间

**测试场景:**
- 设置窗口营业时间为 06:00-09:00，当前时间为 08:00 → 显示"营业中"
- 当前时间为 14:00 → 显示"已暂停营业，营业时间为 06:00-09:00"
- 未设置营业时间 → 显示"营业中"（全天营业）
- 管理员编辑窗口营业时间 → 保存成功

**验证:** 不同时间访问食堂页面，窗口营业状态显示正确。

---

### U10. 食堂公告功能

**目标:** 管理员可发布食堂公告，用户在食堂页面查看

**依赖:** U1

**文件:**
- `backend/src/main/java/com/canteen/system/entity/Announcement.java`（新建）
- `backend/src/main/java/com/canteen/system/mapper/AnnouncementMapper.java`（新建）
- `backend/src/main/java/com/canteen/system/service/AnnouncementService.java`（新建）
- `backend/src/main/java/com/canteen/system/service/impl/AnnouncementServiceImpl.java`（新建）
- `backend/src/main/java/com/canteen/system/controller/AnnouncementController.java`（新建）
- `frontend/src/views/Canteen.vue`（修改）
- `frontend/src/views/admin/Announcements.vue`（新建）
- `frontend/src/router/index.js`（修改：新增路由）

**方案:**

后端 API：
1. `GET /api/announcements/canteen/{canteenId}` — 获取该食堂有效公告（未过期，按置顶+时间排序）
2. `POST /api/announcements` — 新增公告（@RequireAdmin）
3. `PUT /api/announcements/{id}` — 编辑公告（@RequireAdmin）
4. `DELETE /api/announcements/{id}` — 删除公告（@RequireAdmin）

公告有效期判断：`start_time <= NOW() <= end_time`，无时间限制则永久有效。

前端 Canteen.vue：
- 食堂信息区下方显示公告列表（el-alert 或卡片）
- 置顶公告带特殊标记
- 每条公告显示标题 + 发布时间，点击展开内容

前端 admin/Announcements.vue：
- 公告列表表格（标题、所属食堂、置顶状态、有效期、操作）
- 新增/编辑对话框：标题、内容（el-input type=textarea）、所属食堂（下拉）、是否置顶、有效期（el-date-picker range）

**测试场景:**
- 发布公告后，对应食堂页面显示该公告
- 过期公告不显示
- 置顶公告排在最前
- 管理员可编辑/删除公告
- 删除公告后用户页面不再显示

**验证:** 管理员发布公告 → 用户在食堂页面看到 → 过期后自动消失。

---

## 范围边界

### 包含在内
- 以上 10 个实施单元的所有功能
- 数据库初始化脚本同步更新
- 前端路由同步更新

### 延迟到后续工作
- 菜品营养信息（热量、蛋白质等）— 可作为独立功能后续添加
- 用户头像上传（当前只有 URL 字段）
- 窗口实时排队人数
- 评价图片预览优化（多图轮播）
- 移动端适配（响应式优化）

### 超出产品范围
- 微信小程序版本
- 支付/外卖功能
- 供应商管理系统
- 库存管理

---

## 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| Excel 导入数据量大导致超时 | 批量导入失败 | 限制单次导入 ≤ 200 行，超时设置 30s |
| 楼层变更影响现有窗口数据 | 数据不一致 | 减少楼层时校验目标楼层无窗口 |
| 安全问题答案明文存储 | 安全性低 | 使用 BCrypt 加密存储答案 |
| POI 库体积较大 | 打包体积增加 | 使用 poi-ooxml-lite 或按需引入 |

---

## 依赖关系

```
U1 (DB Schema) ──┬── U2 (忘记密码后端) ── U3 (忘记密码前端)
                 ├── U4 (楼层管理后端) ──┬── U5 (食堂详情页)
                 │                       ├── U6 (首页评价分组)
                 │                       ├── U7 (批量导入)
                 │                       ├── U8 (食堂管理页)
                 │                       └── U9 (窗口营业时间)
                 └── U10 (公告功能)
```

建议实施顺序：U1 → U2/U4/U10（可并行） → U3/U5/U6/U7/U8/U9（依赖后端完成后并行）

---

## 建议增加的功能

以下是老师可能认可的额外改进点，可按需选择：

1. **菜品口味标签云** — 搜索页展示热门口味标签，点击快速筛选
2. **食堂拥挤度提示** — 基于评价时段统计，在食堂卡片上显示"当前高峰/较为空闲"
3. **菜品收藏排行** — 首页增加"最多收藏"榜单
4. **评价图片展示** — 评价列表中展示用户上传的图片缩略图
5. **食堂地图导航** — 在食堂详情页显示位置描述或简单示意图

这些功能不影响核心架构，可在以上主体功能完成后按需添加。
