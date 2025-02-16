package io.goorm.youtube.dto;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface VideoMainDTO {
    Long getVideoSeq();
    String getVideo();
    String getVideoThumnail();
    String getTitle();
    LocalDateTime getRegDate();

    @Value("#{@dateFormatter.formatDateTime(target.regDate)}")
    String getFormattedRegDate();

    // 추가: 비디오 상태 관련 필드
    int getPublishYn();

}
