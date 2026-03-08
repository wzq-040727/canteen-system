package com.canteen.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.dto.*;
import com.canteen.system.entity.Dish;
import com.canteen.system.entity.Review;
import com.canteen.system.entity.ReviewLike;
import com.canteen.system.entity.UserBehavior;
import com.canteen.system.mapper.DishMapper;
import com.canteen.system.mapper.ReviewLikeMapper;
import com.canteen.system.mapper.ReviewMapper;
import com.canteen.system.mapper.UserBehaviorMapper;
import com.canteen.system.service.DishService;
import com.canteen.system.service.ReviewService;
import com.canteen.system.service.UserBehaviorService;
import com.canteen.system.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {
    
    private final DishService dishService;
    private final UserBehaviorService userBehaviorService;
    private final ReviewLikeMapper reviewLikeMapper;
    
    @Override
    @Transactional
    public void addReview(ReviewDTO reviewDTO, Long userId) {
        Long existCount = this.count(new LambdaQueryWrapper<Review>()
                .eq(Review::getUserId, userId)
                .eq(Review::getDishId, reviewDTO.getDishId()));
        if (existCount > 0) {
            throw new RuntimeException("您已经评价过该菜品");
        }
        
        Review review = new Review();
        BeanUtil.copyProperties(reviewDTO, review);
        review.setUserId(userId);
        review.setStatus(1);
        review.setLikeCount(0);
        this.save(review);
        
        dishService.updateRating(reviewDTO.getDishId());
        
        userBehaviorService.recordBehavior(userId, reviewDTO.getDishId(), 3, reviewDTO.getRating().doubleValue());
        userBehaviorService.recordBehavior(userId, reviewDTO.getDishId(), 4, 1.0);
    }
    
    @Override
    public List<Review> getDishReviews(Long dishId) {
        List<Review> reviews = baseMapper.selectByDishId(dishId);
        Long currentUserId = UserContext.getCurrentUserId();
        
        if (currentUserId != null) {
            for (Review review : reviews) {
                Long likeCount = reviewLikeMapper.selectCount(new LambdaQueryWrapper<ReviewLike>()
                        .eq(ReviewLike::getUserId, currentUserId)
                        .eq(ReviewLike::getReviewId, review.getId()));
                review.setIsLiked(likeCount > 0);
            }
        }
        
        return reviews;
    }
    
    @Override
    public List<Review> getRecentReviews(int limit) {
        return baseMapper.selectRecentReviews(limit);
    }
    
    @Override
    @Transactional
    public void likeReview(Long reviewId, Long userId) {
        Review review = this.getById(reviewId);
        if (review == null) {
            throw new RuntimeException("评论不存在");
        }
        
        Long existCount = reviewLikeMapper.selectCount(new LambdaQueryWrapper<ReviewLike>()
                .eq(ReviewLike::getUserId, userId)
                .eq(ReviewLike::getReviewId, reviewId));
        if (existCount > 0) {
            throw new RuntimeException("已经点赞过了");
        }
        
        ReviewLike like = new ReviewLike();
        like.setUserId(userId);
        like.setReviewId(reviewId);
        reviewLikeMapper.insert(like);
        
        review.setLikeCount(review.getLikeCount() + 1);
        this.updateById(review);
    }
    
    @Override
    @Transactional
    public void unlikeLikeReview(Long reviewId, Long userId) {
        Review review = this.getById(reviewId);
        if (review == null) {
            throw new RuntimeException("评论不存在");
        }
        
        int deleted = reviewLikeMapper.delete(new LambdaQueryWrapper<ReviewLike>()
                .eq(ReviewLike::getUserId, userId)
                .eq(ReviewLike::getReviewId, reviewId));
        
        if (deleted > 0 && review.getLikeCount() > 0) {
            review.setLikeCount(review.getLikeCount() - 1);
            this.updateById(review);
        }
    }
    
    @Override
    public PageResult<Review> getReviewPage(Long dishId, int pageNum, int pageSize) {
        Page<Review> page = this.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getDishId, dishId)
                        .eq(Review::getStatus, 1)
                        .orderByDesc(Review::getCreatedTime));
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }
    
    @Override
    public void deleteReview(Long id) {
        Review review = this.getById(id);
        if (review != null) {
            this.removeById(id);
            dishService.updateRating(review.getDishId());
        }
    }
    
    @Override
    public void auditReview(Long id, Integer status) {
        Review review = this.getById(id);
        if (review != null) {
            review.setStatus(status);
            this.updateById(review);
        }
    }
}
