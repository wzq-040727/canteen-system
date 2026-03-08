package com.canteen.system.controller;

import com.canteen.system.dto.Result;
import com.canteen.system.entity.Canteen;
import com.canteen.system.service.CanteenService;
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
    
    @GetMapping("/{id}")
    public Result<Canteen> getById(@PathVariable Long id) {
        return Result.success(canteenService.getById(id));
    }
}
