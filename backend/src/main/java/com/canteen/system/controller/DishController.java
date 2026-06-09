package com.canteen.system.controller;

import com.canteen.system.annotation.RequireAdmin;
import com.canteen.system.dto.*;
import com.canteen.system.entity.Dish;
import com.canteen.system.service.DishService;
import com.canteen.system.service.UserBehaviorService;
import com.canteen.system.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {
    
    private final DishService dishService;
    private final UserBehaviorService userBehaviorService;
    
    @GetMapping
    public Result<PageResult<Dish>> query(DishQueryDTO queryDTO) {
        return Result.success(dishService.queryDishes(queryDTO));
    }
    
    //菜品详情接口：根据菜品ID查询菜品详情，同时记录用户行为（点击）,behavior_type=1,score=1.0
    @GetMapping("/{id}")
    public Result<Dish> getDetail(@PathVariable Long id) {
        Dish dish = dishService.getDetail(id);
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            userBehaviorService.recordBehavior(userId, id, 1, 1.0);
        }
        return Result.success(dish);
    }

    @GetMapping("/{id}/smart-review")
    public Result<DishSmartReviewVO> getSmartReview(@PathVariable Long id) {
        return Result.success(dishService.getSmartReview(id));
    }
    
    //菜品推荐接口：根据用户行为推荐Top-N菜品
    @GetMapping("/top")
    public Result<List<Dish>> getTopDishes(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(dishService.getTopDishes(limit));
    }
    
    @GetMapping("/recommend")
    public Result<List<Dish>> getRecommendations(@RequestParam(defaultValue = "10") int limit) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.success(dishService.getTopDishes(limit));
        }
        return Result.success(dishService.getRecommendations(userId, limit));
    }

    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> getCategoryStats() {
        return Result.success(dishService.getCategoryStats());
    }
    
    @PostMapping
    @RequireAdmin(message = "需要管理员权限才能添加菜品")
    public Result<Void> addDish(@RequestBody DishDTO dishDTO) {
        dishService.addDish(dishDTO);
        return Result.success();
    }
    
    @PutMapping
    @RequireAdmin(message = "需要管理员权限才能修改菜品")
    public Result<Void> updateDish(@RequestBody DishDTO dishDTO) {
        dishService.updateDish(dishDTO);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @RequireAdmin(message = "需要管理员权限才能删除菜品")
    public Result<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return Result.success();
    }

    @PostMapping("/import")
    @RequireAdmin(message = "需要管理员权限才能导入菜品")
    public Result<Map<String, Object>> importDishes(@RequestParam("file") MultipartFile file) {
        return Result.success(dishService.importDishes(file));
    }
}
