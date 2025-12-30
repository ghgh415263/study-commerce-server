package com.example.study.product.command.domain.review;

import com.example.study.common.persistance.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadFile extends BaseEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UploadFileStatus status;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public static UploadFile createTemp(UUID id, String storedFileName, String originalFileName, LocalDateTime localDateTime){
        UploadFile file = new UploadFile();
        file.id = id;
        file.storedFileName = storedFileName;
        file.originalFileName = originalFileName;
        file.status = UploadFileStatus.TEMP;
        file.expiredAt = localDateTime;
        return file;
    }
}