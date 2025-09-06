package com.example.study.common;

import lombok.Getter;

import java.util.List;

@Getter
public class CustomPage<T> {

    private List<T> content;

    private CustomPageable customPageable;

    /**
     * @param content 실제 데이터 리스트
     * @param page 1-based 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     */
    public CustomPage(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.customPageable = new CustomPageable(page, size, totalElements);
    }

    public CustomPage(List<T> content) {
        this.content = content;
    }

    @Getter
    public static class CustomPageable {

        private boolean first;
        private boolean last;
        private boolean hasNext;
        private boolean hasPrevious;
        private int totalPages;
        private long totalElements;
        private int page; // 1-based
        private int size;

        public CustomPageable(int page, int size, long totalElements) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;

            // 총 페이지 수 계산
            this.totalPages = (int) Math.ceil((double) totalElements / size);

            this.first = page == 1;
            this.last = page >= totalPages;
            this.hasPrevious = page > 1;
            this.hasNext = page < totalPages;
        }
    }
}
