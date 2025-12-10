package com.example.study.integration;

import com.example.study.common.file.FileStoreClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "file.review-image-dir=${java.io.tmpdir}/review-test/"
})
class FileStoreClientTest {

    @Autowired
    FileStoreClient FileStoreClient;

    @Test
    void 파일이_정상적으로_저장된다() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "hello".getBytes()
        );

        // when
        String storedFileName = FileStoreClient.store(file);

        // then
        Path savedPath = Paths.get(
                System.getProperty("java.io.tmpdir"),
                "review-test",
                storedFileName
        );

        assertThat(Files.exists(savedPath)).isTrue();
    }

    @Test
    void 파일이_삭제된다() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "hello".getBytes()
        );

        String storedFileName = FileStoreClient.store(file);

        Path savedPath = Paths.get(
                System.getProperty("java.io.tmpdir"),
                "review-test",
                storedFileName
        );

        assertThat(Files.exists(savedPath)).isTrue();

        // when
        FileStoreClient.delete(storedFileName);

        // then
        assertThat(Files.exists(savedPath)).isFalse();
    }
}
