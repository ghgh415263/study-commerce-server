package com.example.study.unit.common;

import com.example.study.common.util.UUIDUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UUIDUtilsTest {

    @Test
    @DisplayName("UUID를 byte[16]으로 변환하고 다시 UUID로 복원할 수 있다")
    void testToBytesAndFromBytes() {
        // given
        UUID original = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // when
        byte[] bytes = UUIDUtils.toBytes(original);
        UUID restored = UUIDUtils.fromBytes(bytes);

        // then
        assertNotNull(bytes);
        assertEquals(16, bytes.length);
        assertEquals(original, restored);
    }

    @Test
    @DisplayName("null byte 배열은 IllegalArgumentException을 발생시킨다")
    void testFromBytesWithNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> UUIDUtils.fromBytes(null)
        );
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    @DisplayName("byte 배열 길이가 16이 아니면 IllegalArgumentException 발생")
    void testFromBytesWithInvalidLength() {
        byte[] invalid = new byte[5];

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> UUIDUtils.fromBytes(invalid)
        );

        assertTrue(ex.getMessage().contains("16바이트"));
    }
}
