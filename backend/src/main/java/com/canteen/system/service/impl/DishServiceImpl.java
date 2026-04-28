package com.canteen.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.dto.*;
import com.canteen.system.entity.Dish;
import com.canteen.system.entity.Review;
import com.canteen.system.entity.UserBehavior;
import com.canteen.system.mapper.DishMapper;
import com.canteen.system.mapper.ReviewMapper;
import com.canteen.system.mapper.UserBehaviorMapper;
import com.canteen.system.service.DishService;
import com.canteen.system.util.QueryTextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    private final ReviewMapper reviewMapper;
    private final UserBehaviorMapper userBehaviorMapper;

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
}
