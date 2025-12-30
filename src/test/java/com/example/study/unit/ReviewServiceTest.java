package com.example.study.unit;

import com.example.study.common.file.FileStoreClient;
import com.example.study.product.command.application.review.ReviewCreateRequestDto;
import com.example.study.product.command.application.review.ReviewService;
import com.example.study.product.command.domain.product.DeliveryProduct;
import com.example.study.product.command.domain.product.ProductRepository;
import com.example.study.product.command.domain.product.ProductStatus;
import com.example.study.product.command.domain.review.Review;
import com.example.study.product.command.domain.review.ReviewImage;
import com.example.study.product.command.domain.review.ReviewImageRepository;
import com.example.study.product.command.domain.review.ReviewRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ReviewImageRepository reviewImageRepository;

    @Autowired
    ProductRepository productRepository;

    @MockitoBean
    FileStoreClient fileStoreClient;

    private DeliveryProduct givenProduct(){
        DeliveryProduct deliveryProduct =  new DeliveryProduct("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다."
                , ProductStatus.SOLD_OUT.name()
                , 1000
                , 10);

        productRepository.save(deliveryProduct);

        return deliveryProduct;
    }

    @Test
    void 리뷰저장시_텍스트와_업로드된_이미지가_저장된다() {
        // given
        DeliveryProduct deliveryProduct = givenProduct();

        List<String> uploadTempImageName = new ArrayList<>();
        uploadTempImageName.add("test1");
        uploadTempImageName.add("test2");
        uploadTempImageName.add("test3");

        ReviewCreateRequestDto dto = new ReviewCreateRequestDto(
                deliveryProduct.getId(),
                "좋아요",
                5,
                uploadTempImageName
        );

        // when
        reviewService.saveReview(1L, dto);

        // then
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(1);

        Review savedReview = reviews.get(0);
        assertThat(savedReview.getContent()).isEqualTo("좋아요");
        assertThat(savedReview.getStar()).isEqualTo(5);
        assertThat(savedReview.getProductId()).isEqualTo(deliveryProduct.getId());

        // 리뷰 이미지가 리뷰에 연결됐는지
        List<ReviewImage> images = reviewImageRepository.findAll();
        assertThat(images).hasSize(3);

        // 모든 이미지가 방금 저장한 리뷰에 속하는지
        assertThat(images)
                .allMatch(img -> img.getReview().getId().equals(savedReview.getId()));

        // 이미지 파일명이 요청값과 동일한지
        assertThat(images)
                .extracting(ReviewImage::getStoredFileName)
                .containsExactlyInAnyOrder("test1", "test2", "test3");
    }

    @Test
    void 리뷰삭제시_텍스트와_이미지가_삭제된다() {
        // given
        DeliveryProduct deliveryProduct = givenProduct();

        Review review = new Review(1L, deliveryProduct.getId(), "리뷰", 5);
        ReviewImage image = new ReviewImage(review,"stored.png");
        review.addImage(image);

        reviewRepository.save(review);

        willDoNothing().given(fileStoreClient).delete(any());

        // when
        reviewService.deleteReview(1L, review.getId());

        // then
        verify(fileStoreClient).delete("stored.png");
        assertThat(reviewRepository.existsById(review.getId())).isFalse();
        assertThat(reviewImageRepository.existsById(image.getId())).isFalse();
    }
}