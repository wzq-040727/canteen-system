package com.canteen.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.entity.Dish;
import com.canteen.system.entity.Favorite;
import com.canteen.system.mapper.DishMapper;
import com.canteen.system.mapper.FavoriteMapper;
import com.canteen.system.service.FavoriteService;
import com.canteen.system.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {
    
    private final UserBehaviorService userBehaviorService;
    
    @Override
    public void addFavorite(Long userId, Long dishId) {
        if (isFavorite(userId, dishId)) {
            throw new RuntimeException("已经收藏过了");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setDishId(dishId);
        this.save(favorite);
        
        userBehaviorService.recordBehavior(userId, dishId, 2, 2.0);
    }
    
    @Override
    public void removeFavorite(Long userId, Long dishId) {
        this.remove(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getDishId, dishId));
    }
    
    @Override
    public boolean isFavorite(Long userId, Long dishId) {
        return this.count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getDishId, dishId)) > 0;
    }
    
    @Override
    public List<Favorite> getUserFavorites(Long userId) {
        return this.list(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreatedTime));
    }
}
