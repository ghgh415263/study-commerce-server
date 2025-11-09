package com.example.study.product.command.application.review;

import com.example.study.product.command.application.product.ProductNotFoundException;
import com.example.study.product.command.domain.product.ProductRepository;
import com.example.study.product.command.domain.review.Review;
import com.example.study.product.command.domain.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProductRepository productRepository;

    /**
     * 리뷰 저장
     * @param memberId
     * @param dto
     * @return
     */
    @Transactional
    public Long saveReview(Long memberId , ReviewRequestDto dto){
        productRepository.findById(dto.productId())
                .orElseThrow(ProductNotFoundException::new);
        Review review = new Review(memberId, dto.productId(), dto.content(), dto.star());
        return reviewRepository.save(review).getId();
    }

    /**
     * 리뷰 수정
     * @param reviewId
     * @param dto
     * @return
     */
    @Transactional
    public Long updateReview(Long memberId, Long reviewId, ReviewRequestDto dto){
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        if (!review.getMemberId().equals(memberId))
            throw new InvalidReviewAuthenticationException();
        review.update(dto.content(), dto.star());
        return review.getId();
    }

    /**
     * 리뷰 삭제
     * @param reviewId
     */
    @Transactional
    public void deleteReview(Long memberId, Long reviewId){
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        if (!review.getMemberId().equals(memberId))
            throw new InvalidReviewAuthenticationException();
        reviewRepository.delete(review);
    }
}
