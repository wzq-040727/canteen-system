package com.canteen.system.controller;

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
    public Result<List<Window>> listByCanteen(@PathVariable Long canteenId) {
        return Result.success(windowService.listByCanteenId(canteenId));
    }
}
