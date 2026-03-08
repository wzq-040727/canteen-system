package com.canteen.system.controller;

import com.canteen.system.annotation.RequireAdmin;
import com.canteen.system.dto.*;
import com.canteen.system.entity.Dish;
import com.canteen.system.service.DishService;
import com.canteen.system.service.UserBehaviorService;
import com.canteen.system.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    
    @GetMapping("/{id}")
    public Result<Dish> getDetail(@PathVariable Long id) {
        Dish dish = dishService.getDetail(id);
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            userBehaviorService.recordBehavior(userId, id, 1, 1.0);
        }
        return Result.success(dish);
    }
    
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
}
