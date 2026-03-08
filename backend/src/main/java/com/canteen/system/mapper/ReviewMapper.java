package com.canteen.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.canteen.system.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    
    @Select("SELECT r.*, u.username as user_name, u.avatar as user_avatar, d.name as dish_name " +
            "FROM review r " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "LEFT JOIN dish d ON r.dish_id = d.id " +
            "WHERE r.dish_id = #{dishId} AND r.deleted = 0 AND r.status = 1 " +
            "ORDER BY r.created_time DESC")
    List<Review> selectByDishId(Long dishId);
    
    @Select("SELECT r.*, u.username as user_name, u.avatar as user_avatar, d.name as dish_name " +
            "FROM review r " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "LEFT JOIN dish d ON r.dish_id = d.id " +
            "WHERE r.deleted = 0 AND r.status = 1 " +
            "ORDER BY r.created_time DESC " +
            "LIMIT #{limit}")
    List<Review> selectRecentReviews(int limit);
}
