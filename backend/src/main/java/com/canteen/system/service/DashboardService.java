package com.canteen.system.service;

import com.canteen.system.dto.DashboardVO;

public interface DashboardService {
    DashboardVO getDashboard();
    DashboardVO getCanteenDashboard(Long canteenId);
}
