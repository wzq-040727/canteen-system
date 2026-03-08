package com.canteen.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.system.entity.UserBehavior;

public interface UserBehaviorService extends IService<UserBehavior> {
    void recordBehavior(Long userId, Long dishId, Integer behaviorType, Double score);
}
