# feat: 首页 UI 改进 — 侧边导航、评价多列、新增推荐/公告/分类区块

**Status:** active
**Date:** 2026-06-04
**Depth:** Standard

---

## Summary

全面改进首页体验：新增左侧固定锚点导航栏（6 个入口）、热门推荐菜品图片缩小并增至 12 道、最新评价改为按食堂分列展示、新增个性化推荐 / 最新公告 / 菜品分类三个区块。涉及后端 2 个新接口 + 前端 Home.vue 重构。

## Problem Frame

当前首页存在以下体验问题：
1. 菜品卡片图片过大（180px 高），首屏内容有限
2. 只有 3 个区块垂直堆叠，缺少导航，用户需要滚动才能找到目标
3. 最新评价单列展示，无法同时对比不同食堂的评价
4. 缺少个性化推荐、公告通知、菜品分类筛选等功能入口

## Requirements

- **R1.** 左侧固定锚点导航栏，包含 6 个入口，点击平滑滚动到对应区块，滚动时自动高亮
- **R2.** 热门推荐菜品图片缩小，卡片更紧凑，数量增至 12 道
- **R3.** 最新评价改为按食堂分列的多列布局
- **R4.** 新增"个性化推荐"区块（登录用户基于行为推荐，未登录用户回退到热门推荐）
- **R5.** 新增"最新公告"区块，展示所有食堂的有效公告
- **R6.** 新增"菜品分类"区块，展示分类标签，点击跳转到搜索页筛选
- **R7.** 移动端（<768px）隐藏侧边导航，保持单列布局

## Key Technical Decisions

### KTD-1. 侧边导航实现方式

**决策：** CSS `position: sticky` + `IntersectionObserver` 自动高亮。

**理由：** `sticky` 不遮挡 header，无需计算偏移。`IntersectionObserver` 监听区块可见性，比 scroll 事件更高效。

### KTD-2. 评价多列布局策略

**决策：** 按食堂名称分列，CSS Grid `repeat(auto-fit, minmax(300px, 1fr))` 实现。

**理由：** 用户需求是对比不同食堂评价，按食堂分列最直观。`auto-fit` 天然支持响应式。

### KTD-3. 菜品分类区块交互

**决策：** 展示分类标签云（标签 + 数量），点击后跳转到 `/search?category=xxx`。

**理由：** 后端 `DishMapper.selectCategoryStats()` 已存在，只需暴露接口。复用现有搜索页，无需新建页面。

**替代方案：** 首页内嵌分类筛选 + 菜品列表 — 改动过大，与搜索页功能重复。

### KTD-4. 个性化推荐与热门推荐的关系

**决策：** 两个区块独立展示。个性化推荐在前（更相关），热门推荐在后。未登录用户隐藏个性化推荐区块。

**理由：** `/api/dishes/recommend` 未登录时自动回退到热门推荐，但两个区块展示相同内容无意义，所以未登录时只显示热门推荐。

---

## Implementation Units

### U1. 后端：新增"获取全部公告"接口

**Goal:** 提供 `GET /api/announcements` 接口，返回所有食堂的有效公告

**Requirements:** R5

**Dependencies:** 无

**Files:**
- `backend/src/main/java/com/canteen/system/service/AnnouncementService.java` — 新增 `getAllValid()` 方法
- `backend/src/main/java/com/canteen/system/service/impl/AnnouncementServiceImpl.java` — 实现 `getAllValid()`
- `backend/src/main/java/com/canteen/system/controller/AnnouncementController.java` — 新增 `GET /` 端点

**Approach:**
- `AnnouncementService` 新增 `List<Announcement> getAllValid()` 方法
- `AnnouncementServiceImpl.getAllValid()` 复用 `getValidByCanteenId` 的查询逻辑，去掉 `canteenId` 过滤条件，按 `isTop` 降序 + `createdTime` 降序排列
- `AnnouncementController` 新增 `@GetMapping` 端点，调用 `getAllValid()`

**Patterns to follow:**
- 复用 `getValidByCanteenId` 的时间范围过滤逻辑（`startTime <= now AND endTime >= now`）
- 使用 `LambdaQueryWrapper` 构建查询条件

**Test scenarios:**
- 调用 `GET /api/announcements` 返回所有未过期、未删除的公告
- 公告按置顶优先、创建时间倒序排列
- 过期公告（`endTime < now`）不返回
- 未开始公告（`startTime > now`）不返回

### U2. 后端：新增"获取菜品分类"接口

**Goal:** 提供 `GET /api/dishes/categories` 接口，返回菜品分类及其数量

**Requirements:** R6

**Dependencies:** 无

**Files:**
- `backend/src/main/java/com/canteen/system/service/DishService.java` — 新增 `getCategoryStats()` 方法
- `backend/src/main/java/com/canteen/system/service/impl/DishServiceImpl.java` — 实现 `getCategoryStats()`
- `backend/src/main/java/com/canteen/system/controller/DishController.java` — 新增 `GET /categories` 端点

**Approach:**
- `DishService` 新增 `List<Map<String, Object>> getCategoryStats()` 方法
- `DishServiceImpl` 调用已有的 `dishMapper.selectCategoryStats()`
- `DishController` 新增 `@GetMapping("/categories")` 端点
- 返回格式：`[{ "category": "川菜", "count": 15 }, ...]`

**Patterns to follow:**
- `DishMapper.selectCategoryStats()` 已有 SQL：`SELECT category, COUNT(*) FROM dish WHERE deleted=0 AND status=1 GROUP BY category`
- Controller 使用 `Result.success()` 包装返回

**Test scenarios:**
- 调用 `GET /api/dishes/categories` 返回所有有效菜品的分类及数量
- 已删除菜品和下架菜品不计入统计
- `category` 为 null 的菜品应被排除或归为"其他"

### U3. 前端：添加左侧锚点导航栏

**Goal:** 在首页左侧添加固定锚点导航，6 个入口，支持平滑滚动和自动高亮

**Requirements:** R1, R7

**Dependencies:** 无

**Files:**
- `frontend/src/views/Home.vue` (template + script + style)

**Approach:**
- 添加 `.home-layout` 容器，包含 `.side-nav`（160px 宽）和 `.home-content`
- 导航项：🔥 热门推荐、🎯 个性化推荐、📍 食堂列表、🏷️ 菜品分类、📢 最新公告、💬 最新评价
- 使用 `scrollIntoView({ behavior: 'smooth' })` 实现平滑滚动
- 使用 `IntersectionObserver` 监听 6 个区块的可见性，自动高亮当前区块
- 样式：`position: sticky; top: 80px`，左侧竖线装饰，高亮色 `#667eea`
- 未登录时隐藏"个性化推荐"导航项（区块也隐藏）
- `@media (max-width: 768px)` 隐藏导航栏

**Test scenarios:**
- 点击每个导航项，页面平滑滚动到对应区块
- 滚动页面时，当前可见区块对应的导航项自动高亮
- 未登录时，"个性化推荐"导航项不显示
- 窗口 <768px 时导航栏隐藏
- 导航栏在滚动时保持 sticky 可见

### U4. 前端：调整热门推荐卡片尺寸和数量

**Goal:** 缩小菜品卡片图片，推荐数量增至 12 道

**Requirements:** R2

**Dependencies:** 无

**Files:**
- `frontend/src/views/Home.vue` (script: API 参数, style)
- `frontend/src/style.css` (dish-grid, dish-image)
- `frontend/src/components/DishCardSkeleton.vue` (骨架屏高度同步)

**Approach:**
- API 调用从 `/dishes/top?limit=6` 改为 `/dishes/top?limit=12`
- `.dish-grid` 的 `minmax(280px, 1fr)` 改为 `minmax(200px, 1fr)`
- `.dish-image` 高度从 180px 改为 120px
- 骨架屏 `.skeleton-image` 高度同步改为 120px

**Test scenarios:**
- 首页展示 12 道推荐菜品（不足 12 道时展示全部）
- 卡片图片高度 120px，视觉更紧凑
- 骨架屏高度与实际卡片一致
- 宽屏 4-5 列，窄屏自动减少列数

### U5. 前端：新增个性化推荐区块

**Goal:** 登录用户展示基于行为的个性化推荐菜品

**Requirements:** R4

**Dependencies:** 无

**Files:**
- `frontend/src/views/Home.vue` (template + script)

**Approach:**
- 新增 `personalDishes` ref，调用 `/api/dishes/recommend?limit=6`
- 区块位于热门推荐之前，使用同样的 `.dish-grid` 布局
- 未登录时（`userStore.isLoggedIn` 为 false）隐藏整个区块
- 复用 `DishCardSkeleton` 作为加载态

**Test scenarios:**
- 登录用户看到个性化推荐区块，展示 6 道推荐菜品
- 未登录用户看不到个性化推荐区块
- 推荐接口失败时区块隐藏（不显示错误）

### U6. 前端：新增菜品分类区块

**Goal:** 展示菜品分类标签云，点击跳转搜索页筛选

**Requirements:** R6

**Dependencies:** U2（后端分类接口）

**Files:**
- `frontend/src/views/Home.vue` (template + script + style)

**Approach:**
- 新增 `categories` ref，调用 `/api/dishes/categories`
- 使用标签云样式展示，每个标签显示分类名 + 菜品数量
- 点击标签跳转 `/search?category=xxx`
- 位于食堂列表之后、最新公告之前

**Test scenarios:**
- 首页展示所有菜品分类及数量
- 点击分类标签跳转到搜索页并自动筛选该分类
- 分类接口失败时区块隐藏

### U7. 前端：新增最新公告区块

**Goal:** 展示所有食堂的有效公告

**Requirements:** R5

**Dependencies:** U1（后端公告接口）

**Files:**
- `frontend/src/views/Home.vue` (template + script + style)

**Approach:**
- 新增 `announcements` ref，调用 `/api/announcements`
- 使用卡片列表展示，每条公告显示：置顶标记、标题、食堂名、时间
- 位于菜品分类之后、最新评价之前
- 置顶公告用特殊样式标记（如左侧橙色边框）

**Test scenarios:**
- 首页展示所有有效公告
- 置顶公告排在前面，有视觉区分
- 公告接口失败时区块隐藏

### U8. 前端：最新评价改为多列布局

**Goal:** 将评价从单列改为按食堂分列的网格布局

**Requirements:** R3

**Dependencies:** 无

**Files:**
- `frontend/src/views/Home.vue` (template + style)

**Approach:**
- 评价区域外层添加 CSS Grid：`grid-template-columns: repeat(auto-fit, minmax(300px, 1fr))`
- 每个食堂占一个 grid cell（一列），列内保持楼层→窗口分组
- 每列设置 `max-height: 600px; overflow-y: auto` 防止过长
- 移动端自动回退为单列

**Test scenarios:**
- 3 个食堂有评价时展示为 3 列
- 1 个食堂有评价时展示为 1 列
- 每列内评价按楼层→窗口正确分组
- 列内容过长时可独立滚动

---

## High-Level Technical Design

### 首页布局结构

```
┌─────────────────────────────────────────────────────┐
│                    Header (fixed)                    │
├────────┬────────────────────────────────────────────┤
│        │  ┌─ 🎯 个性化推荐 (登录用户可见) ────────┐ │
│  Side  │  │  [卡片] [卡片] [卡片] [卡片] [卡片]  │ │
│  Nav   │  └────────────────────────────────────────┘ │
│        │  ┌─ 🔥 热门推荐 ─────────────────────────┐ │
│ (sticky)│  │  [卡片] [卡片] [卡片] [卡片]         │ │
│        │  │  [卡片] [卡片] [卡片] [卡片]         │ │
│        │  │  [卡片] [卡片] [卡片] [卡片]         │ │
│        │  └────────────────────────────────────────┘ │
│        │  ┌─ 📍 食堂列表 ─────────────────────────┐ │
│        │  │  [卡片] [卡片] [卡片]                  │ │
│        │  └────────────────────────────────────────┘ │
│        │  ┌─ 🏷️ 菜品分类 ─────────────────────────┐ │
│        │  │  [川菜(15)] [湘菜(8)] [粤菜(12)] ... │ │
│        │  └────────────────────────────────────────┘ │
│        │  ┌─ 📢 最新公告 ─────────────────────────┐ │
│        │  │  📌 置顶公告标题  食堂名  时间         │ │
│        │  │  普通公告标题    食堂名  时间          │ │
│        │  └────────────────────────────────────────┘ │
│        │  ┌─ 💬 最新评价 (多列) ──────────────────┐ │
│        │  │  食堂A列    │  食堂B列    │  食堂C列   │ │
│        │  │  ┌─评价─┐  │  ┌─评价─┐  │  ┌─评价─┐ │ │
│        │  │  └──────┘  │  └──────┘  │  └──────┘ │ │
│        │  └────────────────────────────────────────┘ │
└────────┴────────────────────────────────────────────┘
```

---

## Scope Boundaries

### In Scope
- 首页 Home.vue 模板、脚本、样式重构
- 后端新增 2 个接口（全部公告、菜品分类）
- 全局样式 dish-grid / dish-image 尺寸调整
- 骨架屏组件高度同步调整

### Deferred to Follow-Up Work
- 菜品分类标签的高级筛选（多选、口味筛选）— 当前只支持单分类跳转
- 公告详情弹窗 — 当前只展示标题列表
- 导航栏收起/展开动画 — 当前为固定展示

### Out of Scope
- 其他页面的布局调整
- 后端推荐算法优化
- 新增数据库表或字段

---

## Verification

1. 启动后端（`mvn spring-boot:run`）和前端（`npm run dev`）
2. 访问首页，验证左侧导航栏 6 个入口（未登录时 5 个）
3. 验证个性化推荐区块（登录后可见）
4. 验证热门推荐展示 12 道菜品，图片明显更紧凑
5. 验证食堂列表正常展示
6. 验证菜品分类标签云，点击跳转搜索页
7. 验证最新公告区块，置顶公告在前
8. 验证最新评价按食堂分列展示
9. 缩小窗口到 <768px，验证导航栏隐藏、布局回退单列
