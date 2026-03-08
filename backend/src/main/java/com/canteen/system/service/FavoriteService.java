package com.canteen.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.system.entity.Favorite;
import java.util.List;

public interface FavoriteService extends IService<Favorite> {
    void addFavorite(Long userId, Long dishId);
    void removeFavorite(Long userId, Long dishId);
    boolean isFavorite(Long userId, Long dishId);
    List<Favorite> getUserFavorites(Long userId);
}
