package com.example.study.product.command.application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProductUtils {
    /**
     * 날짜 포매터, yyyy-MM-dd HH:mm:ss
     * @param localDateTime
     * @return String "yyyy-MM-dd HH:mm:ss" 형식 날짜
     */
    public static String dateFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
