package com.example.study.order.command.domain;

import java.util.List;

public interface ProductClient {

    // 재고 감소
    void decreaseStocks(List<ProductStockRequest> stockRequests);

    // 상품 가격 조회
    List<OrderedProduct> getOrderedProducts(List<Long> productIds);

    record ProductStockRequest(Long productId, int quantity) {}
    record OrderedProduct(Long productId, int price) {}
}