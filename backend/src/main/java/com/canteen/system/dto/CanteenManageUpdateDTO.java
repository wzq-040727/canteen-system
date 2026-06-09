package com.canteen.system.dto;

import lombok.Data;

@Data
public class CanteenManageUpdateDTO {
    private Long id;
    private String openingHours;
    private Integer status;
}
