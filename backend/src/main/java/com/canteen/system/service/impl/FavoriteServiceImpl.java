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
    
    //收藏菜品服务：负责处理用户收藏菜品的增删改查
    // 1. 添加收藏菜品：根据用户ID和菜品ID，创建并保存新的收藏记录
    // 2. 删除收藏菜品：根据用户ID和菜品ID，删除该用户的收藏记录
    // 3. 查询用户收藏菜品：根据用户ID，查询该用户收藏的所有菜品
    // 4. 判断用户是否收藏菜品：根据用户ID和菜品ID，判断该用户是否收藏了该菜品
    // 5. 查询用户收藏菜品列表：根据用户ID，查询该用户收藏的所有菜品，按收藏时间降序排序
    // behavior_type=2,score=2.0
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
