package com.canteen.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dish")
public class Dish {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long windowId;
    private Long canteenId;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private String category;
    private String taste;
    private BigDecimal avgRating;
    private Integer ratingCount;
    private Integer status;
    private Integer isRecommend;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String windowName;
    @TableField(exist = false)
    private String canteenName;
}
