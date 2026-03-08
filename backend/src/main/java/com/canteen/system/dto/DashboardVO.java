package com.canteen.system.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardVO {
    private Long totalDishes;
    private Long totalReviews;
    private Long totalUsers;
    private Long totalCanteens;
    private BigDecimal avgRating;
    private Object topDishes;
    private Object recentReviews;
    private Object ratingDistribution;
}
