package com.canteen.system.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DishDTO {
    private Long id;
    private Long windowId;
    private Long canteenId;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private String category;
    private String taste;
    private Integer status;
    private Integer isRecommend;
    private Integer sortOrder;
}
