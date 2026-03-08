package com.canteen.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.system.annotation.RequireAdmin;
import com.canteen.system.dto.PageResult;
import com.canteen.system.dto.Result;
import com.canteen.system.entity.Review;
import com.canteen.system.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews/admin")
@RequiredArgsConstructor
@RequireAdmin(message = "需要管理员权限才能访问评价管理")
public class ReviewAdminController {
    
    private final ReviewMapper reviewMapper;
    
    @GetMapping("/list")
    public Result<PageResult<Review>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Review> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<Review>()
                .orderByDesc(Review::getCreatedTime);
        
        Page<Review> result = reviewMapper.selectPage(page, wrapper);
        
        for (Review review : result.getRecords()) {
            review.setUserName("用户" + review.getUserId());
            review.setDishName("菜品" + review.getDishId());
        }
        
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize()));
    }
}
