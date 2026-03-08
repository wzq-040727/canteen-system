package com.canteen.system.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DishQueryDTO {
    private Long canteenId;
    private Long windowId;
    private String name;
    private String category;
    private String taste;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
