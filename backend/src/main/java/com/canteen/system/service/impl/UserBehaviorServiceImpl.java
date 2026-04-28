package com.canteen.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.entity.UserBehavior;
import com.canteen.system.mapper.UserBehaviorMapper;
import com.canteen.system.service.UserBehaviorService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

//用户行为记录服务实现类：负责处理用户行为记录的增删改查
// 1. 记录用户行为：根据用户ID、菜品ID、行为类型和分数，创建并保存新的用户行为记录
// 2. 查询用户偏好：根据用户ID，查询该用户对所有菜品的总分数，按分数排序返回Top-N
// 3. 协同过滤推荐菜品：根据用户行为，使用协同过滤算法推荐Top-N菜品
@Service
public class UserBehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehavior> implements UserBehaviorService {
    
    @Override
    public void recordBehavior(Long userId, Long dishId, Integer behaviorType, Double score) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setDishId(dishId);
        behavior.setBehaviorType(behaviorType);
        behavior.setScore(BigDecimal.valueOf(score));
        this.save(behavior);
    }
}
