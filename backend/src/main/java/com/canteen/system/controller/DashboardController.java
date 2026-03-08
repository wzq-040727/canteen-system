package com.canteen.system.controller;

import com.canteen.system.dto.DashboardVO;
import com.canteen.system.dto.Result;
import com.canteen.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping
    public Result<DashboardVO> getDashboard() {
        return Result.success(dashboardService.getDashboard());
    }
    
    @GetMapping("/canteen/{canteenId}")
    public Result<DashboardVO> getCanteenDashboard(@PathVariable Long canteenId) {
        return Result.success(dashboardService.getCanteenDashboard(canteenId));
    }
}
