package com.canteen.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_behavior")
public class UserBehavior {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long dishId;
    private Integer behaviorType;
    private BigDecimal score;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
