package com.example.study.product.command.application.product;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

import java.util.Set;

/**
 * 상품 하나에 동일한 상품 태그를 등록 했을때 발생하는 예외입니다.
 */
public class DuplicateProductTagsException extends CustomException {
    /**
     *
     * @param duplicateTags 중복 등록된 상품 태그 set
     */
    public DuplicateProductTagsException(Set<String> duplicateTags) {
        super("상품 태그는 중복으로 등록할 수 없습니다. 중복 태그 : " + duplicateTags,  HttpStatus.BAD_REQUEST);
    }
}
