package com.canteen.system.controller;

import com.canteen.system.dto.Result;
import com.canteen.system.entity.Dish;
import com.canteen.system.entity.Favorite;
import com.canteen.system.service.DishService;
import com.canteen.system.service.FavoriteService;
import com.canteen.system.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    private final DishService dishService;
    
    @PostMapping("/{dishId}")
    public Result<Void> addFavorite(@PathVariable Long dishId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        favoriteService.addFavorite(userId, dishId);
        return Result.success();
    }
    
    @DeleteMapping("/{dishId}")
    public Result<Void> removeFavorite(@PathVariable Long dishId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        favoriteService.removeFavorite(userId, dishId);
        return Result.success();
    }
    
    @GetMapping("/check/{dishId}")
    public Result<Map<String, Boolean>> checkFavorite(@PathVariable Long dishId) {
        Long userId = UserContext.getCurrentUserId();
        Map<String, Boolean> result = new HashMap<>();
        if (userId == null) {
            result.put("isFavorite", false);
        } else {
            result.put("isFavorite", favoriteService.isFavorite(userId, dishId));
        }
        return Result.success(result);
    }
    
    @GetMapping
    public Result<List<Dish>> getUserFavorites() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        List<Favorite> favorites = favoriteService.getUserFavorites(userId);
        List<Dish> dishes = favorites.stream()
                .map(f -> dishService.getById(f.getDishId()))
                .filter(d -> d != null)
                .collect(Collectors.toList());
        return Result.success(dishes);
    }
}
