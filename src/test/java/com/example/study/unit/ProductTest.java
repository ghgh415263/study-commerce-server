package com.example.study.unit;

import com.example.study.integration.TestPersistenceAuditorConfig;
import com.example.study.product.command.application.product.InvalidProductParameterException;
import com.example.study.product.command.domain.product.DeliveryProduct;
import com.example.study.product.command.domain.product.DuplicateProductTagsException;
import com.example.study.product.command.domain.product.ProductStatus;
import com.example.study.product.command.domain.product.ProductTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(TestPersistenceAuditorConfig.class)
@DataJpaTest
public class ProductTest {

    @Test
    @DisplayName("상품 등록시 중복된 상품태그를 입력하면 DuplicateProductTagsException 예외가 발생한다.")
    void saveDuplicateProductTag(){
        // given
        DeliveryProduct deliveryProduct = new DeliveryProduct("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다."
                , ProductStatus.SOLD_OUT.name()
                , 1000
                , 10);

        //when
        List<ProductTag> ProductTagList = List.of(
                new ProductTag("일리"),
                new ProductTag("커피머신"),
                new ProductTag("일리상품"),
                new ProductTag("일리"),
                new ProductTag("커피머신")
        );

        // then
        assertThatThrownBy( () -> deliveryProduct.assignProductTags(ProductTagList))
                .isInstanceOf(DuplicateProductTagsException.class)
                .hasMessageContaining("상품 태그는 중복으로 등록할 수 없습니다. 중복 태그 : ");
    }

    @Test
    @DisplayName("이미 등록된 상품 태그와 추가할 상품 태그가 같을 경우 DuplicateProductTagsException 예외가 발생한다.")
    void addDuplicateProductTag(){
        // given
        DeliveryProduct deliveryProduct = new DeliveryProduct("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다."
                , ProductStatus.SOLD_OUT.name()
                , 1000
                , 10);

        //when
        List<ProductTag> ProductTagList = List.of(
                new ProductTag("일리"),
                new ProductTag("커피머신"),
                new ProductTag("일리상품")
        );
        deliveryProduct.assignProductTags(ProductTagList);

        // then
        assertThatThrownBy(() -> deliveryProduct.assignProductTags(
                List.of(new ProductTag("일리"),new ProductTag("커피머신"))))
                .isInstanceOf(DuplicateProductTagsException.class)
                .hasMessageContaining("상품 태그는 중복으로 등록할 수 없습니다. 중복 태그 : ");
    }

    @Test
    @DisplayName("재고 감소 - 정상")
    void decreaseStock() {
        DeliveryProduct deliveryProduct = new DeliveryProduct("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다."
                , ProductStatus.SOLD_OUT.name()
                , 1000
                , 10);

        deliveryProduct.decreaseStock(2);

        assertEquals(0, deliveryProduct.getStockQuantity());
    }

    @Test
    @DisplayName("재고 감소 - 음수되면 예외 발생")
    void decreaseStock_negative_should_throw() {
        DeliveryProduct deliveryProduct = new DeliveryProduct("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다."
                , ProductStatus.SOLD_OUT.name()
                , 1000
                , 10);

        assertThrows(
                InvalidProductParameterException.class,
                () -> deliveryProduct.decreaseStock(3)
        );
    }

    @Test
    @DisplayName("재고 증가 - 정상")
    void increaseStock() {
        DeliveryProduct deliveryProduct = new DeliveryProduct("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다."
                , ProductStatus.SOLD_OUT.name()
                , 1000
                , 10);

        deliveryProduct.increaseStock(2);

        assertEquals(4, deliveryProduct.getStockQuantity());
    }

    @Test
    @DisplayName("재고 증가 - 음수면 예외 발생")
    void increaseStock_negative_should_throw() {
        DeliveryProduct deliveryProduct = new DeliveryProduct("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다."
                , ProductStatus.SOLD_OUT.name()
                , 1000
                , 10);

        assertThrows(
                InvalidProductParameterException.class,
                () -> deliveryProduct.increaseStock(-3)
        );
    }
}