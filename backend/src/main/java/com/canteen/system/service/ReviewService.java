package com.canteen.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.system.dto.*;
import com.canteen.system.entity.Review;
import java.util.List;

public interface ReviewService extends IService<Review> {
    void addReview(ReviewDTO reviewDTO, Long userId);
    List<Review> getDishReviews(Long dishId);
    List<Review> getRecentReviews(int limit);
    void likeReview(Long reviewId, Long userId);
    void unlikeLikeReview(Long reviewId, Long userId);
    PageResult<Review> getReviewPage(Long dishId, int pageNum, int pageSize);
    void deleteReview(Long id);
    void auditReview(Long id, Integer status);
}
