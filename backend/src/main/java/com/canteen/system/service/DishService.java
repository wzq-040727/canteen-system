package com.canteen.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.system.dto.*;
import com.canteen.system.entity.Dish;
import java.util.List;

public interface DishService extends IService<Dish> {
    PageResult<Dish> queryDishes(DishQueryDTO queryDTO);
    Dish getDetail(Long id);
    List<Dish> getTopDishes(int limit);
    List<Dish> getRecommendations(Long userId, int limit);
    void addDish(DishDTO dishDTO);
    void updateDish(DishDTO dishDTO);
    void deleteDish(Long id);
    void updateRating(Long dishId);
}
