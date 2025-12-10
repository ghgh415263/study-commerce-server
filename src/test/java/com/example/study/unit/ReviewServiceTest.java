package com.example.study.unit;

import com.example.study.common.file.FileStoreClient;
import com.example.study.product.command.application.review.ReviewRequestDto;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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
    void 리뷰저장시_텍스트와_이미지가_저장된다() {
        // given
        DeliveryProduct deliveryProduct = givenProduct();

        ReviewRequestDto dto = new ReviewRequestDto(
                deliveryProduct.getId(),
                "좋아요",
                5
        );

        MockMultipartFile image = new MockMultipartFile(
                "newImages",
                "test.png",
                "image/png",
                "image-data".getBytes()
        );

        given(fileStoreClient.store(any())).willReturn("stored-test.png");

        // when
        reviewService.saveReview(1L, dto, List.of(image));

        // then
        verify(fileStoreClient, times(1)).store(any());
        assertThat(reviewImageRepository.findAll()).hasSize(1);
    }

    @Test
    void 리뷰삭제시_텍스트와_이미지가_삭제된다() {
        // given
        DeliveryProduct deliveryProduct = givenProduct();

        Review review = new Review(1L, deliveryProduct.getId(), "리뷰", 5);
        ReviewImage image = new ReviewImage(review,"origin.png", "stored.png");
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