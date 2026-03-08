package com.canteen.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.system.dto.PageResult;
import com.canteen.system.dto.Result;
import com.canteen.system.entity.Review;
import com.canteen.system.mapper.ReviewMapper;
import com.canteen.system.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews/my")
@RequiredArgsConstructor
public class MyReviewController {
    
    private final ReviewMapper reviewMapper;
    
    @GetMapping
    public Result<List<Review>> list() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        List<Review> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getUserId, userId)
                        .orderByDesc(Review::getCreatedTime)
        );
        return Result.success(reviews);
    }
}
