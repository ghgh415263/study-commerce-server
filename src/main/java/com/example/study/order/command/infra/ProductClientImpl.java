package com.example.study.order.command.infra;

import com.example.study.common.InvalidArgumentException;
import com.example.study.order.command.domain.ProductClient;
import com.example.study.product.command.domain.product.Product;
import com.example.study.product.command.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ProductClientImpl implements ProductClient {

    private final ProductRepository productRepository;

    @Override
    public void decreaseStocks(List<ProductStockRequest> stockRequests) {
        if (stockRequests == null || stockRequests.isEmpty()) {
            return;
        }

        // 요청된 상품 ID 추출
        List<Long> productIds = stockRequests.stream()
                .map(ProductStockRequest::productId)
                .toList();

        // 상품 조회 + 비관적 락
        List<Product> products = productRepository.findAllByIds(productIds);

        // Map으로 쉽게 조회
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 재고 감소 처리
        for (ProductStockRequest req : stockRequests) {
            Product product = productMap.get(req.productId());
            if (product == null) {
                throw new InvalidArgumentException("상품 없음: " + req.productId());
            }

            if (product.getStockQuantity() < req.quantity()) {
                throw new InvalidArgumentException("재고 부족: " + req.productId());
            }

            product.assignStockQuantity(product.getStockQuantity() - req.quantity());
        }
    }

    @Override
    public List<OrderedProduct> getOrderedProducts(List<Long> productIds) {
        return productRepository.findAllByIds(productIds).stream()
                .map(p -> new OrderedProduct(p.getId(), p.getPrice(), p.getProductType().name()))
                .collect(Collectors.toList());
    }
}
