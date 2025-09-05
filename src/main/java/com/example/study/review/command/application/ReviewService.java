package com.example.study.review.command.application;

import com.example.study.member.command.application.MemberNotFoundException;
import com.example.study.member.command.domain.Member;
import com.example.study.member.command.domain.MemberRepository;
import com.example.study.review.command.domain.Review;
import com.example.study.review.command.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final MemberRepository memberRepository;

    /**
     * 상품 상세 리뷰 목록 조회
     * @param productId
     * @return
     */
    public List<ReviewResponseDto> getProductReviews(Long productId, int page) {
        List<ReviewResponseDto> result =  reviewRepository.findByProductIdWithPaging(productId, page)
                .stream()
                .map(ReviewResponseDto::from)
                .toList();

        return result;
    }

    /**
     * 리뷰 저장
     * @param memberId
     * @param dto
     * @return
     */
    @Transactional
    public Long saveReview(UUID memberId , ReviewRequestDto dto){
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Review review = new Review(member.getLoginId(), dto.productId(), dto.content(), dto.star());
        return reviewRepository.save(review).getId();
    }

    /**
     * 리뷰 수정
     * @param reviewId
     * @param dto
     * @return
     */
    @Transactional
    public Long updateReview(UUID memberId, Long reviewId, ReviewRequestDto dto){
        Review review = reviewRepository.findByReviewId(reviewId).orElseThrow(ReviewNotFoundException::new);
        validReviewLoginId(memberId, review);
        review.update(dto.content(), dto.star());
        return review.getId();
    }

    /**
     * 리뷰 삭제
     * @param reviewId
     */
    @Transactional
    public void deleteReview(UUID memberId, Long reviewId){
        Review review = reviewRepository.findByReviewId(reviewId).orElseThrow(ReviewNotFoundException::new);
        validReviewLoginId(memberId, review);
        reviewRepository.delete(review);
    }

    private void validReviewLoginId(UUID memberId, Review review){
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        if(!member.getLoginId().equals(review.getLoginId())){
            throw new InvalidReviewAuthenticationException();
        }
    }
}
