package com.example.study.common.util;

public class MaskingUtils {
    /**
     * 아이디 마스킹, 앞 2글자, 뒤 2글자 뺴고는 * 처리
     * (3글자 이하는 글자수대로 마스킹)
     * @param memberId
     * @return 마스킹Id
     */
    public static String maskId(String memberId) {
        if(memberId == null){
            throw new IllegalArgumentException("System Error... memberId is not null");
        }

        int idSize = memberId.length();

        if(idSize <= 3){
            return "*".repeat(idSize);
        }

        String startId = memberId.substring(0, 2);
        String endId = memberId.substring(idSize -2);
        String masked = "*".repeat(memberId.length() - 4);

        return startId + masked + endId;
    }
}
