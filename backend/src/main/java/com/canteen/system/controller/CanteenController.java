package com.canteen.system.controller;

import com.canteen.system.annotation.RequireAdmin;
import com.canteen.system.dto.CanteenManageUpdateDTO;
import com.canteen.system.dto.Result;
import com.canteen.system.entity.Canteen;
import com.canteen.system.service.CanteenService;
import com.canteen.system.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/canteens")
@RequiredArgsConstructor
public class CanteenController {
    
    private final CanteenService canteenService;
    
    @GetMapping
    public Result<List<Canteen>> list() {
        return Result.success(canteenService.listAll());
    }
    
    @GetMapping("/{id:\\d+}")
    public Result<Canteen> getById(@PathVariable Long id) {
        return Result.success(canteenService.findById(id));
    }

    @GetMapping("/admin/list")
    @RequireAdmin(message = "需要管理员权限才能查看食堂管理数据")
    public Result<List<Canteen>> manageList() {
        return Result.success(canteenService.listForManage());
    }

    @PutMapping("/admin/update")
    @RequireAdmin(message = "需要管理员权限才能修改食堂信息")
    public Result<Void> updateForManage(@RequestBody CanteenManageUpdateDTO dto) {
        canteenService.updateByRole(dto, UserContext.getCurrentUserRole());
        return Result.success();
    }
}
