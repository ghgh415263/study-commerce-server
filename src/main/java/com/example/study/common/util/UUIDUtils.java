package com.example.study.common.util;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * UUID <-> byte[16] 변환 유틸리티.
 *
 * RDBMS에서 UUID를 문자열(36 bytes)로 저장하면 공간 낭비가 크기 때문에
 * 바이너리(16 bytes) 형태로 저장하려는 경우 유용하다.
 * MySQL의 BINARY(16), PostgreSQL의 uuid 타입 등과 연계해서 사용 가능하다.
 *
 * 본 클래스는 순수 유틸리티이므로 인스턴스 생성을 금지
 */
public final class UUIDUtils {

    /**
     * 객체 생성 방지.
     */
    private UUIDUtils() {
    }

    /**
     * UUID → byte[16] 변환.
     *
     * UUID는 내부적으로 128비트 구조이며,
     * 상위 64비트(MSB), 하위 64비트(LSB) 두 개의 long 값으로 표현된다.
     * ByteBuffer에 long 두 개를 그대로 기록하여 16바이트 배열로 만든다.
     *
     * @param uuid 변환할 UUID (null 가능)
     * @return 16바이트 배열 또는 null
     */
    public static byte[] toBytes(UUID uuid) {
        if (uuid == null) return null;

        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    /**
     * byte[16] → UUID 변환.
     *
     * 저장소에서 읽어온 16바이트 데이터를 다시 UUID 객체로 복원한다.
     * ByteBuffer에서 long 두 개를 읽어 UUID(MSB, LSB) 생성자를 호출한다.
     *
     * @param bytes UUID를 나타내는 16바이트 배열
     * @return UUID 객체
     * @throws IllegalArgumentException bytes가 null이거나 길이가 16이 아닐 때
     */
    public static UUID fromBytes(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("UUID 바이트 배열은 null일 수 없습니다.");
        }
        if (bytes.length != 16) {
            throw new IllegalArgumentException("UUID 바이트 배열은 16바이트여야 합니다. length=" + bytes.length);
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long high = buffer.getLong();
        long low = buffer.getLong();
        return new UUID(high, low);
    }
}
