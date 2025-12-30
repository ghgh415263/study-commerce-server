package com.example.study.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    /**
     * 객체 생성 방지
     */
    private DateUtils() {}

    /**
     * 날짜 포매터, yyyy-MM-dd HH:mm:ss
     * @param localDateTime
     * @return String "yyyy-MM-dd HH:mm:ss" 형식 날짜
     */
    public static String dateFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 나노 초 절삭
     * @param localDateTime
     * @return LocalDateTime "yyyy-MM-dd HH:mm:ss"
     */
    public static LocalDateTime truncateToSeconds(LocalDateTime localDateTime) {
        return localDateTime.truncatedTo(ChronoUnit.SECONDS);
    }

}
