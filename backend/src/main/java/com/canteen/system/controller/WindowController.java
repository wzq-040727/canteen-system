package com.canteen.system.controller;

import com.canteen.system.annotation.RequireAdmin;
import com.canteen.system.dto.Result;
import com.canteen.system.entity.Window;
import com.canteen.system.service.WindowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/windows")
@RequiredArgsConstructor
public class WindowController {

    private final WindowService windowService;

    @GetMapping("/canteen/{canteenId}")
    public Result<List<Window>> listByCanteen(@PathVariable Long canteenId,
                                               @RequestParam(required = false) Integer floor) {
        if (floor != null) {
            return Result.success(windowService.listByCanteenIdAndFloor(canteenId, floor));
        }
        return Result.success(windowService.listByCanteenId(canteenId));
    }

    @GetMapping("/canteen/{canteenId}/floors")
    public Result<List<Integer>> getFloors(@PathVariable Long canteenId) {
        return Result.success(windowService.getFloorsByCanteenId(canteenId));
    }

    @PostMapping
    @RequireAdmin(message = "需要管理员权限才能添加窗口")
    public Result<Void> addWindow(@RequestBody Window window) {
        windowService.addWindow(window);
        return Result.success();
    }

    @PutMapping("/{id}")
    @RequireAdmin(message = "需要管理员权限才能修改窗口")
    public Result<Void> updateWindow(@PathVariable Long id, @RequestBody Window window) {
        window.setId(id);
        windowService.updateWindow(window);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequireAdmin(message = "需要管理员权限才能删除窗口")
    public Result<Void> deleteWindow(@PathVariable Long id) {
        windowService.deleteWindow(id);
        return Result.success();
    }
}
