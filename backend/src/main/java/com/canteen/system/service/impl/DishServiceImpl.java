package com.canteen.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.dto.*;
import com.canteen.system.entity.Canteen;
import com.canteen.system.entity.Dish;
import com.canteen.system.entity.Review;
import com.canteen.system.entity.UserBehavior;
import com.canteen.system.entity.Window;
import com.canteen.system.mapper.CanteenMapper;
import com.canteen.system.mapper.DishMapper;
import com.canteen.system.mapper.ReviewMapper;
import com.canteen.system.mapper.UserBehaviorMapper;
import com.canteen.system.mapper.WindowMapper;
import com.canteen.system.service.DishService;
import com.canteen.system.util.QueryTextUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    private final ReviewMapper reviewMapper;
    private final UserBehaviorMapper userBehaviorMapper;
    private final CanteenMapper canteenMapper;
    private final WindowMapper windowMapper;

    @Override
    public PageResult<Dish> queryDishes(DishQueryDTO queryDTO) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getStatus() != null) {
            wrapper.eq(Dish::getStatus, queryDTO.getStatus());
        }

        if (queryDTO.getCanteenId() != null) {
            wrapper.eq(Dish::getCanteenId, queryDTO.getCanteenId());
        }
        if (queryDTO.getWindowId() != null) {
            wrapper.eq(Dish::getWindowId, queryDTO.getWindowId());
        }
        if (QueryTextUtil.hasText(queryDTO.getName())) {
            wrapper.like(Dish::getName, queryDTO.getName());
        }
        if (QueryTextUtil.hasText(queryDTO.getCategory())) {
            wrapper.eq(Dish::getCategory, queryDTO.getCategory());
        }
        if (QueryTextUtil.hasText(queryDTO.getTaste())) {
            wrapper.like(Dish::getTaste, queryDTO.getTaste());
        }

        wrapper.orderByDesc(Dish::getIsRecommend)
                .orderByDesc(Dish::getAvgRating)
                .orderByDesc(Dish::getRatingCount);

        Page<Dish> page = this.page(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), wrapper);

        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public Dish getDetail(Long id) {
        Dish dish = this.getById(id);
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }
        return dish;
    }

    @Override
    public List<Dish> getTopDishes(int limit) {
        return baseMapper.selectTopDishes(limit);
    }

    // 推荐服务调用流程：
    // 1. 调用协同过滤算法获取推荐结果
    // 2. 冷启动处理：如果协同过滤结果为空，返回热门菜品
    // 3. 提取菜品ID列表
    // 4. 批量查询菜品详情
    // 5. 按推荐顺序返回结果
    @Override
    public List<Dish> getRecommendations(Long userId, int limit) {
        // 1. 调用协同过滤算法获取推荐结果
        List<Map<String, Object>> cfResults = userBehaviorMapper.selectCollaborativeFiltering(userId, limit);

        // 2. 冷启动处理：如果协同过滤结果为空，返回热门菜品
        if (cfResults.isEmpty()) {
            return getTopDishes(limit);
        }

        // 3. 提取菜品ID列表
        List<Long> dishIds = cfResults.stream()
                .map(m -> ((Number) m.get("dish_id")).longValue())
                .collect(Collectors.toList());

        // 4. 批量查询菜品详情
        List<Dish> dishes = this.listByIds(dishIds);

        // 5. 按推荐顺序返回结果
        Map<Long, Dish> dishMap = dishes.stream()
                .collect(Collectors.toMap(Dish::getId, d -> d));

        return dishIds.stream()
                .filter(dishMap::containsKey)
                .map(dishMap::get)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public DishSmartReviewVO getSmartReview(Long dishId) {
        Dish dish = getDetail(dishId);
        List<Review> reviews = reviewMapper.selectByDishId(dishId);
        DishSmartReviewVO vo = new DishSmartReviewVO();
        vo.setReviewCount(reviews.size());

        if (reviews.isEmpty()) {
            String taste = QueryTextUtil.hasText(dish.getTaste()) ? dish.getTaste() : "口味信息待补充";
            vo.setSummary("当前暂无用户评论，推荐优先关注这道菜的口味标签：" + taste + "。如你尝试后欢迎留下第一条评价。");
            vo.setSource("taste");
            return vo;
        }

        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        List<String> topComments = reviews.stream()
                .map(Review::getContent)
                .filter(QueryTextUtil::hasText)
                .limit(3)
                .collect(Collectors.toList());

        String sentiment;
        if (avg >= 4.5) {
            sentiment = "整体口碑非常好";
        } else if (avg >= 4.0) {
            sentiment = "整体评价较好";
        } else if (avg >= 3.0) {
            sentiment = "评价中等";
        } else {
            sentiment = "评价偏谨慎";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("基于").append(reviews.size()).append("条用户评价，")
                .append(sentiment)
                .append("，平均评分约").append(String.format("%.1f", avg)).append("分。");
        if (!topComments.isEmpty()) {
            summary.append("高频反馈：");
            for (int i = 0; i < topComments.size(); i++) {
                if (i > 0) {
                    summary.append("；");
                }
                summary.append(topComments.get(i));
            }
            summary.append("。");
        }

        vo.setSummary(summary.toString());
        vo.setSource("reviews");
        return vo;
    }

    @Override
    public void addDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtil.copyProperties(dishDTO, dish);
        dish.setAvgRating(BigDecimal.ZERO);
        dish.setRatingCount(0);
        this.save(dish);
    }

    @Override
    public void updateDish(DishDTO dishDTO) {
        Dish dish = this.getById(dishDTO.getId());
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }
        BeanUtil.copyProperties(dishDTO, dish, "avgRating", "ratingCount");
        this.updateById(dish);
    }

    @Override
    public void deleteDish(Long id) {
        this.removeById(id);
    }

    @Override
    public void updateRating(Long dishId) {
        List<Review> reviews = reviewMapper.selectByDishId(dishId);

        if (reviews.isEmpty()) {
            Dish dish = new Dish();
            dish.setId(dishId);
            dish.setAvgRating(BigDecimal.ZERO);
            dish.setRatingCount(0);
            this.updateById(dish);
            return;
        }

        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setAvgRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
        dish.setRatingCount(reviews.size());
        this.updateById(dish);
    }

    @Override
    public Map<String, Object> importDishes(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();

            if (lastRow < 1) {
                throw new RuntimeException("文件为空或没有数据行");
            }

            // 通过表头行动态映射列索引，避免因列顺序不同导致读取错误
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> headerMap = new HashMap<>();
            if (headerRow != null) {
                for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                    String val = getCellStringValue(headerRow.getCell(c));
                    if (!val.isEmpty()) {
                        headerMap.put(val, c);
                    }
                }
            }

            int colName     = headerMap.getOrDefault("菜品名称", 0);
            int colCanteen  = headerMap.getOrDefault("食堂名称", 1);
            int colFloor    = headerMap.getOrDefault("楼层", 2);
            int colWindow   = headerMap.getOrDefault("窗口名称", 3);
            int colPrice    = headerMap.getOrDefault("价格", 4);
            int colCategory = headerMap.getOrDefault("分类", 5);
            int colTaste    = headerMap.getOrDefault("口味", 6);
            int colDesc     = headerMap.getOrDefault("描述", 7);

            List<Canteen> canteens = canteenMapper.selectList(null);
            List<Window> windows = windowMapper.selectList(null);

            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String dishName     = getCellStringValue(row.getCell(colName));
                    String canteenName  = getCellStringValue(row.getCell(colCanteen));
                    String floorStr     = getCellStringValue(row.getCell(colFloor));
                    String windowName   = getCellStringValue(row.getCell(colWindow));
                    String priceStr     = getCellStringValue(row.getCell(colPrice));
                    String category     = getCellStringValue(row.getCell(colCategory));
                    String taste        = getCellStringValue(row.getCell(colTaste));
                    String description  = getCellStringValue(row.getCell(colDesc));

                    if (dishName.isEmpty()) {
                        errors.add("第" + (i + 1) + "行：菜品名称为空");
                        failCount++;
                        continue;
                    }

                    Canteen canteen = canteens.stream()
                            .filter(c -> c.getName().equals(canteenName))
                            .findFirst().orElse(null);
                    if (canteen == null) {
                        errors.add("第" + (i + 1) + "行：食堂「" + canteenName + "」不存在");
                        failCount++;
                        continue;
                    }

                    int floorNum = 1;
                    if (!floorStr.isEmpty()) {
                        try {
                            floorNum = Integer.parseInt(floorStr);
                        } catch (NumberFormatException e) {
                            errors.add("第" + (i + 1) + "行：楼层格式不正确");
                            failCount++;
                            continue;
                        }
                    }
                    final int finalFloor = floorNum;

                    Window window = windows.stream()
                            .filter(w -> w.getCanteenId().equals(canteen.getId())
                                    && w.getName().equals(windowName)
                                    && w.getFloor() != null && w.getFloor() == finalFloor)
                            .findFirst().orElse(null);
                    if (window == null) {
                        errors.add("第" + (i + 1) + "行：窗口「" + windowName + "」在" + canteenName + finalFloor + "楼不存在");
                        failCount++;
                        continue;
                    }

                    BigDecimal price;
                    try {
                        price = new BigDecimal(priceStr);
                        if (price.compareTo(BigDecimal.ZERO) <= 0) {
                            errors.add("第" + (i + 1) + "行：价格必须大于0");
                            failCount++;
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        errors.add("第" + (i + 1) + "行：价格格式不正确");
                        failCount++;
                        continue;
                    }

                    Dish dish = new Dish();
                    dish.setName(dishName);
                    dish.setCanteenId(canteen.getId());
                    dish.setWindowId(window.getId());
                    dish.setPrice(price);
                    dish.setCategory(category.isEmpty() ? null : category);
                    dish.setTaste(taste.isEmpty() ? null : taste);
                    dish.setDescription(description.isEmpty() ? null : description);
                    dish.setStatus(1);
                    dish.setAvgRating(BigDecimal.ZERO);
                    dish.setRatingCount(0);
                    this.save(dish);
                    successCount++;
                } catch (Exception e) {
                    errors.add("第" + (i + 1) + "行：" + e.getMessage());
                    failCount++;
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("文件解析失败：" + e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errors", errors);
        return result;
    }

    @Override
    public List<Map<String, Object>> getCategoryStats() {
        return baseMapper.selectCategoryStats();
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num) && !Double.isInfinite(num)) {
                    return String.valueOf((long) num);
                }
                return BigDecimal.valueOf(num).stripTrailingZeros().toPlainString();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    try {
                        return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
                    } catch (Exception e2) {
                        return "";
                    }
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
