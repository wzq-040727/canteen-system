package com.canteen.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.system.dto.*;
import com.canteen.system.entity.Dish;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface DishService extends IService<Dish> {
    PageResult<Dish> queryDishes(DishQueryDTO queryDTO);
    Dish getDetail(Long id);
    List<Dish> getTopDishes(int limit);
    List<Dish> getRecommendations(Long userId, int limit);
    DishSmartReviewVO getSmartReview(Long dishId);
    void addDish(DishDTO dishDTO);
    void updateDish(DishDTO dishDTO);
    void deleteDish(Long id);
    void updateRating(Long dishId);
    Map<String, Object> importDishes(MultipartFile file);
    List<Map<String, Object>> getCategoryStats();
}
