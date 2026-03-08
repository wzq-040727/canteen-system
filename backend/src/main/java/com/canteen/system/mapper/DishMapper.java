package com.canteen.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.canteen.system.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    
    @Select("SELECT d.*, w.name as window_name, c.name as canteen_name " +
            "FROM dish d " +
            "LEFT JOIN `window` w ON d.window_id = w.id " +
            "LEFT JOIN canteen c ON d.canteen_id = c.id " +
            "WHERE d.deleted = 0 AND d.status = 1 " +
            "ORDER BY d.avg_rating DESC, d.rating_count DESC " +
            "LIMIT #{limit}")
    List<Dish> selectTopDishes(int limit);
    
    @Select("SELECT category, COUNT(*) as count FROM dish WHERE deleted = 0 AND status = 1 GROUP BY category")
    List<Map<String, Object>> selectCategoryStats();
}
