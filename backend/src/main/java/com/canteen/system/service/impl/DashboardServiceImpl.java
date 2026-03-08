package com.canteen.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.canteen.system.dto.DashboardVO;
import com.canteen.system.entity.*;
import com.canteen.system.mapper.*;
import com.canteen.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final DishMapper dishMapper;
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;
    private final CanteenMapper canteenMapper;
    
    @Override
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();
        
        vo.setTotalDishes(dishMapper.selectCount(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getStatus, 1)));
        
        vo.setTotalReviews(reviewMapper.selectCount(null));
        
        vo.setTotalUsers(userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getRole, 0)));
        
        vo.setTotalCanteens(canteenMapper.selectCount(new LambdaQueryWrapper<Canteen>()
                .eq(Canteen::getStatus, 1)));
        
        List<Dish> topDishes = dishMapper.selectTopDishes(5);
        vo.setTopDishes(topDishes);
        
        List<Review> recentReviews = reviewMapper.selectRecentReviews(5);
        vo.setRecentReviews(recentReviews);
        
        List<Map<String, Object>> ratingDist = getRatingDistribution();
        vo.setRatingDistribution(ratingDist);
        
        BigDecimal avgRating = calculateOverallAvgRating();
        vo.setAvgRating(avgRating);
        
        return vo;
    }
    
    @Override
    public DashboardVO getCanteenDashboard(Long canteenId) {
        DashboardVO vo = new DashboardVO();
        
        vo.setTotalDishes(dishMapper.selectCount(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getCanteenId, canteenId)
                .eq(Dish::getStatus, 1)));
        
        return vo;
    }
    
    private List<Map<String, Object>> getRatingDistribution() {
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("rating", i);
            item.put("count", reviewMapper.selectCount(new LambdaQueryWrapper<Review>()
                    .eq(Review::getRating, i)));
            result.add(item);
        }
        return result;
    }
    
    private BigDecimal calculateOverallAvgRating() {
        List<Review> reviews = reviewMapper.selectList(null);
        if (reviews.isEmpty()) {
            return BigDecimal.ZERO;
        }
        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        return BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
    }
}
