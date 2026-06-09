const pptxgen = require('pptxgenjs');
const path = require('path');

const pptx = new pptxgen();
pptx.layout = 'LAYOUT_16x9';
pptx.author = '王志强';
pptx.title = '校园食堂智能点评与推荐系统 — 毕业答辩';

// Swiss International Style — IKB Theme
const C = {
  paper: 'FAFAF8',
  ink: '0A0A0A',
  grey1: 'F0F0EE',
  grey2: 'D4D4D2',
  grey3: '737373',
  accent: '002FA7',  // IKB
  accentOn: 'FFFFFF',
};
const F = 'Arial';
const M = 0.6;

// Swiss style helpers — no shadows, no rounded corners, no gradients

function contentBg(slide) {
  slide.background = { color: C.paper };
}

// Swiss action title: large, light weight feel via bold + accent underline
function title(slide, text) {
  contentBg(slide);
  // Accent line at top
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0, y: 0, w: 10, h: 0.04,
    fill: { color: C.accent }
  });
  slide.addText(text, {
    x: M, y: 0.25, w: 8.8, h: 0.7,
    fontSize: 22, fontFace: F, color: C.ink, bold: true
  });
  // Hairline divider
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: M, y: 1.0, w: 8.8, h: 0.015,
    fill: { color: C.grey2 }
  });
}

// Swiss section label
function cat(slide, text, y) {
  slide.addText(text.toUpperCase(), {
    x: M, y: y, w: 4.0, h: 0.3,
    fontSize: 11, fontFace: F, color: C.accent, bold: true, charSpacing: 3
  });
}

// Swiss body text
function body(slide, text, x, y, w, h, opts = {}) {
  slide.addText(text, {
    x, y, w, h,
    fontSize: opts.fontSize || 14, fontFace: F, color: opts.color || C.ink,
    bold: opts.bold || false, lineSpacingMultiple: 1.4,
    valign: opts.valign || 'top'
  });
}

// Swiss bullet list
function bullets(slide, items, y = 1.2, h = 3.5) {
  const flat = [];
  for (const item of items) {
    if (typeof item === 'string') {
      flat.push({ text: item, options: { breakLine: true, bullet: { code: '2013' } } });
    } else {
      flat.push({ text: item.label, options: { bold: true, breakLine: false, bullet: { code: '2013' } } });
      flat.push({ text: item.desc, options: { breakLine: true } });
    }
  }
  slide.addText(flat, {
    x: M, y, w: 8.8, h,
    fontSize: 14, fontFace: F, color: C.ink, paraSpaceAfter: 6, lineSpacingMultiple: 1.4
  });
}

// Swiss footnote
function foot(slide, text) {
  slide.addText(text, {
    x: M, y: 5.2, w: 8.8, h: 0.25,
    fontSize: 10, fontFace: F, color: C.grey3
  });
}

// Swiss table
function table(slide, headers, rows, opts = {}) {
  const { x = M, y = 1.2, w = 8.8, colW } = opts;
  const hRow = headers.map(h => ({
    text: h, options: { fill: { color: C.ink }, color: C.paper, bold: true, fontSize: 12, fontFace: F, align: 'left', valign: 'middle' }
  }));
  const dRows = rows.map((row, ri) => row.map(cell => ({
    text: cell, options: { fill: { color: ri % 2 === 0 ? C.paper : C.grey1 }, color: C.ink, fontSize: 12, fontFace: F, align: 'left', valign: 'middle' }
  })));
  slide.addTable([hRow, ...dRows], {
    x, y, w,
    h: (rows.length + 1) * 0.4,
    colW,
    border: { pt: 0.5, color: C.grey2 },
    rowH: 0.4,
  });
}

// Swiss section divider
function divider(slide, num, title) {
  slide.background = { color: C.ink };
  // Accent bar left
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0, y: 0, w: 0.06, h: 5.625,
    fill: { color: C.accent }
  });
  // Section number
  slide.addText(num, {
    x: 0.8, y: 1.5, w: 2.0, h: 0.6,
    fontSize: 14, fontFace: F, color: C.grey3, charSpacing: 5
  });
  // Title — large, light weight
  slide.addText(title, {
    x: 0.8, y: 2.1, w: 8.4, h: 1.2,
    fontSize: 40, fontFace: F, color: C.paper, bold: false
  });
  // Accent underline
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0.8, y: 3.4, w: 1.5, h: 0.04,
    fill: { color: C.accent }
  });
  // Bottom accent bar
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0, y: 5.585, w: 10, h: 0.04,
    fill: { color: C.accent }
  });
}

// ============================================================
// SLIDES
// ============================================================

// SLIDE 1: Cover — Swiss style
{
  const slide = pptx.addSlide();
  slide.background = { color: C.paper };
  // Top accent bar
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0, y: 0, w: 10, h: 0.06,
    fill: { color: C.accent }
  });
  // Left accent bar
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0, y: 0, w: 0.06, h: 5.625,
    fill: { color: C.accent }
  });
  // Kicker
  slide.addText('毕业设计答辩', {
    x: 0.8, y: 0.8, w: 4.0, h: 0.35,
    fontSize: 12, fontFace: F, color: C.accent, bold: true, charSpacing: 4
  });
  // Main title — large
  slide.addText('校园食堂智能\n点评与推荐系统', {
    x: 0.8, y: 1.3, w: 8.0, h: 1.8,
    fontSize: 44, fontFace: F, color: C.ink, bold: false, lineSpacingMultiple: 1.1
  });
  // Accent rule
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0.8, y: 3.2, w: 2.0, h: 0.05,
    fill: { color: C.accent }
  });
  // Meta info
  slide.addText('答辩人：王志强\n指导教师：王喜凤\n安徽工业大学 计算机科学与技术学院\n2026年5月', {
    x: 0.8, y: 3.5, w: 5.0, h: 1.4,
    fontSize: 13, fontFace: F, color: C.grey3, lineSpacingMultiple: 1.6
  });
  // Bottom accent bar
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0, y: 5.56, w: 10, h: 0.065,
    fill: { color: C.accent }
  });
}

// SLIDE 2: Index
{
  const slide = pptx.addSlide();
  title(slide, '论文结构');
  const sections = [
    { n: '01', t: '研究背景与意义' },
    { n: '02', t: '相关技术综述' },
    { n: '03', t: '系统需求分析' },
    { n: '04', t: '系统设计' },
    { n: '05', t: '系统实现' },
    { n: '06', t: '系统测试' },
    { n: '07', t: '结论与展望' },
  ];
  sections.forEach((s, i) => {
    const y = 1.25 + i * 0.55;
    slide.addText(s.n, {
      x: 0.8, y, w: 0.6, h: 0.4,
      fontSize: 18, fontFace: F, color: C.accent, bold: true
    });
    slide.addShape(pptx.shapes.RECTANGLE, {
      x: 1.5, y: y + 0.18, w: 0.4, h: 0.015,
      fill: { color: C.grey2 }
    });
    slide.addText(s.t, {
      x: 2.1, y, w: 6.0, h: 0.4,
      fontSize: 16, fontFace: F, color: C.ink, valign: 'middle'
    });
  });
}

// SLIDE 3: 研究背景
{
  const slide = pptx.addSlide();
  title(slide, '高校食堂信息化不足，学生就餐体验亟待优化');
  bullets(slide, [
    { label: '信息流通不畅：', desc: '学生难以提前了解菜品供应、价格和评价，只能"盲选"' },
    { label: '反馈渠道缺失：', desc: '缺乏系统化的意见收集机制，服务质量改进无据可依' },
    { label: '服务同质化：', desc: '饮食偏好多元化 vs 餐饮方案趋同，供需差距明显' },
    { label: '移动互联网契机：', desc: '美团等平台经验可借鉴，结合高校场景定制化开发' },
  ]);
  foot(slide, '刘婷,朱亚峰.基于机器学习的智能商品推荐系统设计[J].2025');
}

// SLIDE 4: 研究目的与意义
{
  const slide = pptx.addSlide();
  title(slide, '构建集评价、推荐、管理于一体的食堂综合平台');
  cat(slide, '研究目的', 1.15);
  bullets(slide, [
    '搭建菜品信息聚合展示窗口，提供全面、透明的餐饮信息入口',
    '构建双向互动评价机制，用消费数据驱动服务质量改进',
    '提供个性化菜品推荐，基于协同过滤帮学生快速找到想吃的',
    '为管理方提供数据看板，支撑运营决策',
  ], 1.5, 1.8);
  cat(slide, '实践意义', 3.4);
  bullets(slide, [
    '减少信息不对称带来的无效等待，提升就餐体验',
    '量化评分与评论数据帮助食堂精准改进服务',
  ], 3.75, 0.8);
}

// SLIDE 5: 国内外研究现状
{
  const slide = pptx.addSlide();
  title(slide, '协同过滤是主流推荐算法，校园餐饮推荐仍有空白');
  cat(slide, '推荐系统研究', 1.15);
  bullets(slide, [
    '三大推荐方法：基于内容过滤、协同过滤、混合推荐',
    'Item-based CF被Amazon、Netflix大规模采用',
    '深度学习推荐兴起，但需大量数据和算力，高校场景下传统CF更实用',
  ], 1.5, 1.4);
  cat(slide, '校园餐饮推荐研究', 3.0);
  bullets(slide, [
    '现有研究不足：数据来源单一、工程实现薄弱、冷启动策略不成熟',
    '本系统采用User-based CF，适配高校小规模场景，SQL子查询直接完成',
  ], 3.35, 1.0);
  foot(slide, 'Wang Z. Content-based CF[C]. 2023; 刘鑫.协同过滤技术[J].2024');
}

// SLIDE 6: Divider — 技术综述
{
  const slide = pptx.addSlide();
  divider(slide, 'PART 01', '相关技术综述');
}

// SLIDE 7: 技术选型
{
  const slide = pptx.addSlide();
  title(slide, 'SpringBoot + Vue3 + MySQL 前后端分离架构');
  table(slide,
    ['层次', '技术选型', '作用'],
    [
      ['后端框架', 'SpringBoot 3.2.0', 'RESTful API服务、业务编排'],
      ['数据持久层', 'MyBatis-Plus', 'ORM映射、通用CRUD、分页'],
      ['数据库', 'MySQL 8.0', '全量业务数据存储'],
      ['前端框架', 'Vue3 + ElementPlus', '单页应用、UI组件库'],
      ['状态管理', 'Pinia', '用户状态、Token管理'],
      ['身份认证', 'JWT', '无状态令牌认证'],
      ['推荐算法', 'User-based CF', 'SQL子查询实现协同过滤'],
    ],
    { colW: [2.0, 2.8, 4.0] }
  );
}

// SLIDE 8: 需求分析
{
  const slide = pptx.addSlide();
  title(slide, '三类用户角色，六大功能模块');
  cat(slide, '用户角色', 1.15);
  bullets(slide, [
    { label: '在校学生：', desc: '浏览菜品、评分评论、收藏、获取推荐' },
    { label: '食堂管理员：', desc: '维护食堂/菜品信息、查看数据看板' },
    { label: '系统管理员：', desc: '用户管理、评论审核、权限控制' },
  ], 1.5, 1.4);
  cat(slide, '功能模块', 3.0);
  const mods = ['用户管理', '食堂菜品', '点评互动', '个性化推荐', '收藏', '数据看板'];
  mods.forEach((m, i) => {
    const col = i % 3;
    const row = Math.floor(i / 3);
    const x = 0.8 + col * 2.9;
    const y = 3.4 + row * 0.65;
    slide.addShape(pptx.shapes.RECTANGLE, {
      x, y, w: 2.6, h: 0.5,
      fill: { color: i === 0 ? C.accent : C.grey1 },
      line: { color: C.grey2, width: 0.5 }
    });
    slide.addText(m, {
      x, y, w: 2.6, h: 0.5,
      fontSize: 13, fontFace: F, color: i === 0 ? C.accentOn : C.ink,
      bold: i === 0, align: 'center', valign: 'middle'
    });
  });
}

// SLIDE 9: 非功能需求
{
  const slide = pptx.addSlide();
  title(slide, '安全、易用、可维护、兼容');
  const items = [
    { label: '安全保障', desc: '密码BCrypt加密，API接口JWT校验，管理接口角色判定', c: C.accent },
    { label: '易用性', desc: '界面简洁清晰，核心流程三步内完成', c: C.grey3 },
    { label: '可维护性', desc: '分层架构，职责清晰，便于迭代', c: C.grey3 },
    { label: '兼容适配', desc: 'Chrome/Firefox/Edge主流浏览器，自适应布局', c: C.grey3 },
  ];
  items.forEach((item, i) => {
    const y = 1.25 + i * 0.95;
    // Left accent mark
    slide.addShape(pptx.shapes.RECTANGLE, {
      x: 0.8, y, w: 0.06, h: 0.75,
      fill: { color: item.c }
    });
    slide.addText(item.label, {
      x: 1.1, y: y + 0.02, w: 7.5, h: 0.3,
      fontSize: 15, fontFace: F, color: C.ink, bold: true
    });
    slide.addText(item.desc, {
      x: 1.1, y: y + 0.35, w: 7.5, h: 0.35,
      fontSize: 12, fontFace: F, color: C.grey3
    });
  });
}

// SLIDE 10: Divider — 系统设计
{
  const slide = pptx.addSlide();
  divider(slide, 'PART 02', '系统设计');
}

// SLIDE 11: 系统架构
{
  const slide = pptx.addSlide();
  title(slide, '前后端分离三层架构，职责边界清晰');
  const layers = [
    { name: '表示层 · 前端', desc: 'Vue3 + ElementPlus + Axios + Pinia + Vue Router', color: C.accent },
    { name: '业务逻辑层 · 后端', desc: 'SpringBoot + Controller / Service / Filter + AOP', color: C.ink },
    { name: '数据访问层 · 数据库', desc: 'MyBatis-Plus + MySQL 8.0', color: C.grey3 },
  ];
  layers.forEach((l, i) => {
    const y = 1.3 + i * 1.3;
    slide.addShape(pptx.shapes.RECTANGLE, {
      x: 0.8, y, w: 8.4, h: 1.0,
      fill: { color: l.color }
    });
    slide.addText(l.name, {
      x: 1.1, y: y + 0.1, w: 3.5, h: 0.4,
      fontSize: 15, fontFace: F, color: C.paper, bold: true
    });
    slide.addText(l.desc, {
      x: 1.1, y: y + 0.5, w: 7.8, h: 0.35,
      fontSize: 12, fontFace: F, color: l.color === C.grey3 ? C.paper : 'B0C4DE'
    });
    if (i < 2) {
      slide.addShape(pptx.shapes.RECTANGLE, {
        x: 4.95, y: y + 1.0, w: 0.03, h: 0.3,
        fill: { color: C.grey2 }
      });
    }
  });
  foot(slide, '前后端分离架构：前端SPA + 后端RESTful API + JWT无状态认证');
}

// SLIDE 12: 数据库设计
{
  const slide = pptx.addSlide();
  title(slide, '8张核心数据表，覆盖全部业务实体');
  table(slide,
    ['数据表', '用途', '关键设计'],
    [
      ['user', '用户信息', 'BCrypt密码、三级角色'],
      ['canteen', '食堂信息', '营业状态/时间'],
      ['window', '窗口信息', '食堂ID外键'],
      ['dish', '菜品信息', '冗余avg_rating字段'],
      ['review', '评论数据', '用户+菜品联合唯一'],
      ['user_behavior', '行为记录', '行为类型+权重分数'],
      ['favorite', '收藏关系', '用户+菜品联合唯一'],
      ['review_like', '评论点赞', '用户+评论联合唯一'],
    ],
    { colW: [2.0, 2.0, 4.8] }
  );
}

// SLIDE 13: 推荐算法设计
{
  const slide = pptx.addSlide();
  title(slide, 'User-based协同过滤：SQL子查询完成推荐计算');
  cat(slide, '算法选型理由', 1.15);
  bullets(slide, [
    { label: '实现简洁：', desc: '嵌套SQL查询直接在数据库层面完成，无需额外计算引擎' },
    { label: '场景适配：', desc: '高校用户群体稳定，人数和菜品规模有限，计算量可控' },
    { label: '实时响应：', desc: '每次请求实时查询，用户行为变化立即反映到推荐结果' },
  ], 1.5, 1.8);
  cat(slide, '行为权重设计', 3.4);
  const weights = [
    { name: '浏览', score: '1', c: C.grey3 },
    { name: '收藏', score: '3', c: C.grey3 },
    { name: '评分', score: '4', c: C.accent },
    { name: '评论', score: '5', c: C.accent },
  ];
  weights.forEach((w, i) => {
    const x = 0.8 + i * 2.2;
    slide.addShape(pptx.shapes.RECTANGLE, {
      x, y: 3.85, w: 1.9, h: 0.9,
      fill: { color: C.paper },
      line: { color: w.c, width: 1.5 }
    });
    slide.addText(w.name, {
      x, y: 3.88, w: 1.9, h: 0.35,
      fontSize: 11, fontFace: F, color: C.grey3, align: 'center'
    });
    slide.addText(w.score, {
      x, y: 4.2, w: 1.9, h: 0.5,
      fontSize: 28, fontFace: F, color: w.c, bold: true, align: 'center'
    });
  });
}

// SLIDE 14: 推荐算法流程
{
  const slide = pptx.addSlide();
  title(slide, '四步SQL子查询：找邻居 → 排除已交互 → 排序取Top-N');
  const steps = [
    { n: '01', text: '获取目标用户已交互菜品集合', sql: 'SELECT dish_id FROM user_behavior WHERE user_id = ?' },
    { n: '02', text: '寻找对相同菜品有行为的"邻居用户"', sql: 'SELECT user_id FROM user_behavior WHERE dish_id IN (...) AND user_id != ?' },
    { n: '03', text: '排除目标用户已交互菜品', sql: 'AND dish_id NOT IN (SELECT dish_id FROM user_behavior WHERE user_id = ?)' },
    { n: '04', text: '按行为分数降序取Top-N', sql: 'GROUP BY dish_id ORDER BY SUM(score) DESC LIMIT ?' },
  ];
  steps.forEach((s, i) => {
    const y = 1.2 + i * 0.95;
    slide.addText(s.n, {
      x: 0.8, y, w: 0.5, h: 0.35,
      fontSize: 16, fontFace: F, color: C.accent, bold: true
    });
    slide.addText(s.text, {
      x: 1.4, y, w: 7.5, h: 0.3,
      fontSize: 14, fontFace: F, color: C.ink, bold: true
    });
    slide.addText(s.sql, {
      x: 1.4, y: y + 0.3, w: 7.5, h: 0.3,
      fontSize: 10, fontFace: 'Courier New', color: C.grey3
    });
    if (i < 3) {
      slide.addShape(pptx.shapes.RECTANGLE, {
        x: 1.03, y: y + 0.35, w: 0.015, h: 0.6,
        fill: { color: C.grey2 }
      });
    }
  });
  // Cold start note
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0.8, y: 4.9, w: 8.4, h: 0.4,
    fill: { color: C.grey1 },
    line: { color: C.accent, width: 0.8 }
  });
  slide.addText('冷启动兜底：协同过滤结果为空时，自动退化为热门菜品推荐（全站评分最高Top-N）', {
    x: 1.0, y: 4.9, w: 8.0, h: 0.4,
    fontSize: 11, fontFace: F, color: C.ink, valign: 'middle'
  });
}

// SLIDE 15: Divider — 系统实现
{
  const slide = pptx.addSlide();
  divider(slide, 'PART 03', '系统实现');
}

// SLIDE 16: 用户管理实现
{
  const slide = pptx.addSlide();
  title(slide, 'BCrypt加密 + JWT令牌 + AOP权限三层安全机制');
  const items = [
    { label: '密码安全', desc: 'BCrypt哈希加密，内置随机盐值，抵御彩虹表攻击。数据库只存哈希值。', c: C.accent },
    { label: 'JWT认证', desc: 'Payload携带userId/role，HMAC-SHA256签名。JwtFilter解析Authorization头。', c: C.ink },
    { label: '权限控制', desc: '自定义@RequireAdmin注解 + AOP切面，拦截管理接口判定角色。', c: C.grey3 },
  ];
  items.forEach((item, i) => {
    const y = 1.25 + i * 1.25;
    slide.addShape(pptx.shapes.RECTANGLE, {
      x: 0.8, y, w: 0.06, h: 1.0,
      fill: { color: item.c }
    });
    slide.addText(item.label, {
      x: 1.1, y: y + 0.05, w: 8.0, h: 0.35,
      fontSize: 16, fontFace: F, color: C.ink, bold: true
    });
    slide.addText(item.desc, {
      x: 1.1, y: y + 0.45, w: 8.0, h: 0.45,
      fontSize: 12, fontFace: F, color: C.grey3, lineSpacingMultiple: 1.3
    });
  });
}

// SLIDE 17: 菜品模块
{
  const slide = pptx.addSlide();
  title(slide, '三级层次查询 + 动态筛选 + 冗余评分字段优化性能');
  bullets(slide, [
    { label: '层次结构：', desc: '食堂→窗口→菜品三级数据，LambdaQueryWrapper动态拼接条件' },
    { label: '搜索筛选：', desc: '支持按名称模糊搜索、按食堂/分类/口味组合筛选' },
    { label: '排序策略：', desc: '按推荐状态→平均评分→评分人数降序，优先展示热门菜品' },
    { label: '评分维护：', desc: 'dish表冗余avg_rating字段，评论后同步更新，避免聚合查询' },
  ]);
  foot(slide, 'MyBatis-Plus LambdaQueryWrapper支持链式调用，类型安全');
}

// SLIDE 18: 点评模块
{
  const slide = pptx.addSlide();
  title(slide, '评论发表+点赞互动，@Transactional保证数据一致性');
  cat(slide, '评论发表流程', 1.15);
  bullets(slide, [
    '前端格式校验 → multipart提交 → 后端联合唯一约束检查',
    '@Transactional事务：写入review表 + 更新菜品评分 + 记录行为',
  ], 1.5, 1.0);
  cat(slide, '评论点赞', 2.6);
  bullets(slide, [
    'review_like表联合唯一约束防重复点赞',
    '点赞：插入记录 + like_count递增；取消：删除记录 + 递减',
  ], 2.95, 0.9);
  cat(slide, '评论审核', 3.95);
  bullets(slide, [
    '管理员可审查/屏蔽/删除评论，status=0隐藏，前台不可见',
  ], 4.3, 0.5);
}

// SLIDE 19: 推荐模块实现
{
  const slide = pptx.addSlide();
  title(slide, '三层嵌套SQL子查询实现协同过滤，自动降级兜底');
  bullets(slide, [
    { label: '第一层：', desc: '获取目标用户已交互菜品ID集合' },
    { label: '第二层：', desc: '查询对上述菜品有行为的其他用户（邻居用户）' },
    { label: '外层查询：', desc: '排除已交互菜品，按SUM(score)降序取Top-N' },
  ]);
  cat(slide, '调用链路', 2.8);
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0.8, y: 3.15, w: 8.4, h: 0.7,
    fill: { color: C.grey1 },
    line: { color: C.grey2, width: 0.5 }
  });
  slide.addText('DishController → DishService → UserBehaviorMapper.selectCollaborativeFiltering()\n结果为空时 → DishMapper.selectTopDishes() 热门兜底', {
    x: 1.0, y: 3.2, w: 8.0, h: 0.6,
    fontSize: 11, fontFace: 'Courier New', color: C.ink, lineSpacingMultiple: 1.4
  });
  cat(slide, '行为记录触发', 4.0);
  bullets(slide, [
    '浏览详情(1分) | 收藏(3分) | 评分(评分值) | 评论(1分)',
  ], 4.35, 0.5);
}

// SLIDE 20: 管理端实现
{
  const slide = pptx.addSlide();
  title(slide, '数据看板+菜品/食堂/用户管理，支撑运营决策');
  bullets(slide, [
    { label: '数据看板：', desc: '总菜品数、总评论数、用户数、平均评分、Top菜品排行、评分分布' },
    { label: '菜品管理：', desc: '增删改查，图片上传，逻辑删除保留评论数据' },
    { label: '食堂管理：', desc: '营业状态/时间设置，状态切换轻量接口' },
    { label: '用户管理：', desc: '角色分配（学生/食堂管理员/系统管理员），状态启用/禁用' },
    { label: '评论审核：', desc: '分页审核列表，支持隐藏/显示切换' },
  ]);
}

// SLIDE 21: 前端实现
{
  const slide = pptx.addSlide();
  title(slide, 'Vue Router权限守卫 + Pinia状态管理 + Axios拦截器');
  bullets(slide, [
    { label: '路由守卫：', desc: '15个路由，meta标识requiresAuth/requiresAdmin，全局前置守卫拦截' },
    { label: '状态管理：', desc: 'Pinia setup store，user+token响应式引用，localStorage持久化' },
    { label: 'HTTP封装：', desc: '请求拦截器自动加Bearer令牌，响应拦截器统一处理错误码' },
    { label: '组件化：', desc: 'ElementPlus组件库，SkeletonLoader骨架屏提升加载体验' },
  ]);
}

// SLIDE 22: 系统测试
{
  const slide = pptx.addSlide();
  title(slide, '19个功能测试用例全部通过，100并发性能达标');
  cat(slide, '测试环境', 1.15);
  body(slide, 'Windows 11 · Intel i5 · 16GB · JDK 17 · MySQL 8.0 · Chrome', 0.8, 1.5, 8.4, 0.3, { color: C.grey3, fontSize: 12 });
  cat(slide, '功能测试结果', 2.0);
  table(slide,
    ['模块', '用例数', '结果'],
    [
      ['用户管理', '5', '全部通过'],
      ['点评模块', '5', '全部通过'],
      ['推荐模块', '4', '全部通过'],
      ['菜品管理', '5', '全部通过'],
    ],
    { x: 1.2, y: 2.35, w: 7.6, colW: [2.8, 2.0, 2.8] }
  );
}

// SLIDE 23: 结论
{
  const slide = pptx.addSlide();
  slide.background = { color: C.ink };
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0, y: 0, w: 0.06, h: 5.625,
    fill: { color: C.accent }
  });
  slide.addText('CONCLUSION', {
    x: 0.8, y: 0.4, w: 4.0, h: 0.3,
    fontSize: 12, fontFace: F, color: C.grey3, charSpacing: 5
  });
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0.8, y: 0.75, w: 8.4, h: 0.015,
    fill: { color: C.grey2 }
  });
  const conclusions = [
    '完成了前后端分离的食堂评价与推荐系统，覆盖六大功能模块',
    '实现了基于User-based协同过滤的推荐引擎，SQL子查询数据库层面完成计算',
    '采用BCrypt+JWT+AOP三层安全机制，19个测试用例全部通过',
    '系统在功能完整性、架构合理性、推荐有效性方面达到预期目标',
  ];
  conclusions.forEach((c, i) => {
    const y = 1.0 + i * 0.9;
    slide.addText(String(i + 1).padStart(2, '0'), {
      x: 0.8, y, w: 0.5, h: 0.35,
      fontSize: 18, fontFace: F, color: C.accent, bold: true
    });
    slide.addText(c, {
      x: 1.5, y, w: 7.5, h: 0.7,
      fontSize: 15, fontFace: F, color: C.paper, lineSpacingMultiple: 1.3
    });
  });
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0, y: 5.56, w: 10, h: 0.065,
    fill: { color: C.accent }
  });
}

// SLIDE 24: 不足与展望
{
  const slide = pptx.addSlide();
  title(slide, '系统不足与未来改进方向');
  cat(slide, '当前不足', 1.15);
  bullets(slide, [
    '推荐算法精度有提升空间，仅实现基础User-based CF',
    '冷启动处理较粗糙，热门兜底与个性化推荐差距明显',
    '测试数据为模拟数据，未在真实校园环境验证',
    '移动端适配不足，主要面向PC端',
  ], 1.5, 1.6);
  cat(slide, '未来展望', 3.2);
  bullets(slide, [
    { label: '混合推荐：', desc: 'User-based CF + 基于内容推荐，解决冷启动和数据稀疏' },
    { label: 'Redis缓存：', desc: '缓存热点菜品和推荐结果，减轻数据库压力' },
    { label: '微信小程序：', desc: '利用社交裂变提升用户渗透率' },
    { label: 'NLP评论分析：', desc: '情感分析+主题挖掘，提取更细粒度用户偏好' },
  ], 3.55, 1.5);
}

// SLIDE 25: 参考文献
{
  const slide = pptx.addSlide();
  title(slide, '参考文献');
  const refs = [
    '[1] 刘婷,朱亚峰.基于机器学习的智能商品推荐系统设计[J].中国新技术新产品,2025.',
    '[2] Ricci F, et al. Recommender Systems Handbook[M]. Springer.',
    '[3] Wang Z. A content-based CF algorithm for movies[C]. 2023.',
    '[4] 李淼淼,等.基于SpringBoot和Vue 3的移动学习管理系统[J].无线互联科技,2026.',
    '[5] 刘鑫.协同过滤技术在智能推荐系统中的应用[J].集成电路应用,2024.',
    '[6] 徐富萍,等.个性化推荐算法对用户决策行为影响研究综述[J].计算机科学,2025.',
    '[7] 王蓉,等.基于混合聚类与融合用户属性特征的协同过滤推荐算法[J].现代电子技术,2021.',
  ];
  slide.addText(refs.map(r => ({ text: r, options: { breakLine: true, paraSpaceAfter: 5 } })), {
    x: 0.8, y: 1.2, w: 8.4, h: 4.0,
    fontSize: 11, fontFace: F, color: C.ink, lineSpacingMultiple: 1.4
  });
}

// SLIDE 26: 致谢
{
  const slide = pptx.addSlide();
  slide.background = { color: C.ink };
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0, y: 0, w: 0.06, h: 5.625,
    fill: { color: C.accent }
  });
  slide.addText('THANKS', {
    x: 0.8, y: 1.2, w: 8.0, h: 0.4,
    fontSize: 14, fontFace: F, color: C.grey3, charSpacing: 8
  });
  slide.addText('致谢', {
    x: 0.8, y: 1.7, w: 8.0, h: 1.0,
    fontSize: 44, fontFace: F, color: C.paper, bold: false
  });
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0.8, y: 2.8, w: 1.5, h: 0.04,
    fill: { color: C.accent }
  });
  slide.addText('感谢指导教师王喜凤老师的悉心指导\n感谢计算机学院各位任课教师的教导\n感谢同学朋友的陪伴与鼓励\n感谢父母多年来的支持与关爱', {
    x: 0.8, y: 3.1, w: 8.0, h: 2.0,
    fontSize: 14, fontFace: F, color: 'B0B0B0', lineSpacingMultiple: 1.8
  });
  slide.addShape(pptx.shapes.RECTANGLE, {
    x: 0, y: 5.56, w: 10, h: 0.065,
    fill: { color: C.accent }
  });
}

// Write
const outPath = 'E:/毕设/毕设文档/答辩PPT.pptx';
pptx.writeFile({ fileName: outPath }).then(() => {
  console.log('Done:', outPath);
  console.log('Slides:', pptx.slides.length);
}).catch(err => console.error(err));
