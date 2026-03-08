package com.canteen.system.controller;

import com.canteen.system.annotation.RequireAdmin;
import com.canteen.system.dto.*;
import com.canteen.system.entity.Review;
import com.canteen.system.service.ReviewService;
import com.canteen.system.util.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping
    public Result<Void> addReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        reviewService.addReview(reviewDTO, userId);
        return Result.success();
    }
    
    @GetMapping("/dish/{dishId}")
    public Result<List<Review>> getDishReviews(@PathVariable Long dishId) {
        return Result.success(reviewService.getDishReviews(dishId));
    }
    
    @GetMapping("/recent")
    public Result<List<Review>> getRecentReviews(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(reviewService.getRecentReviews(limit));
    }
    
    @PostMapping("/{reviewId}/like")
    public Result<Void> likeReview(@PathVariable Long reviewId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        reviewService.likeReview(reviewId, userId);
        return Result.success();
    }
    
    @DeleteMapping("/{reviewId}/like")
    public Result<Void> unlikeReview(@PathVariable Long reviewId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        reviewService.unlikeLikeReview(reviewId, userId);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return Result.success();
    }
    
    @PutMapping("/{id}/audit")
    @RequireAdmin(message = "需要管理员权限才能审核评价")
    public Result<Void> auditReview(@PathVariable Long id, @RequestParam Integer status) {
        reviewService.auditReview(id, status);
        return Result.success();
    }
}
