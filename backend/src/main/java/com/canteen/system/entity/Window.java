package com.canteen.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("`window`")
public class Window {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long canteenId;
    private String name;
    private String description;
    private String cuisineType;
    private String image;
    private Integer status;
    private Integer sortOrder;
    private Integer floor;
    private LocalTime openTime;
    private LocalTime closeTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String canteenName;
}
