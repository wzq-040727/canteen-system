package com.canteen.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.canteen.system.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
    
    @Select("<script>" +
            "SELECT dish_id, SUM(score) as total_score FROM user_behavior " +
            "WHERE user_id = #{userId} GROUP BY dish_id ORDER BY total_score DESC" +
            "</script>")
    List<Map<String, Object>> selectUserPreferences(@Param("userId") Long userId);
    
    @Select("<script>" +
            "SELECT ub.dish_id, SUM(ub.score) as score FROM user_behavior ub " +
            "WHERE ub.user_id IN " +
            "(SELECT ub2.user_id FROM user_behavior ub2 WHERE ub2.dish_id IN " +
            "(SELECT ub3.dish_id FROM user_behavior ub3 WHERE ub3.user_id = #{userId}) " +
            "AND ub2.user_id != #{userId}) " +
            "AND ub.dish_id NOT IN (SELECT ub4.dish_id FROM user_behavior ub4 WHERE ub4.user_id = #{userId}) " +
            "GROUP BY ub.dish_id ORDER BY score DESC LIMIT #{limit}" +
            "</script>")
    List<Map<String, Object>> selectCollaborativeFiltering(@Param("userId") Long userId, @Param("limit") int limit);
}
