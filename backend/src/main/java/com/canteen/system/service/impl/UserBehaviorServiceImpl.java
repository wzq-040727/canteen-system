package com.canteen.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.entity.UserBehavior;
import com.canteen.system.mapper.UserBehaviorMapper;
import com.canteen.system.service.UserBehaviorService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

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
