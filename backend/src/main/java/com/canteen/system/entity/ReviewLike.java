package com.canteen.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("review_like")
public class ReviewLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long reviewId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
