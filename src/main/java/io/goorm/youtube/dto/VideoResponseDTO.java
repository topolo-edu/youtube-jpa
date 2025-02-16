package io.goorm.youtube.dto;


import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;


public interface VideoResponseDTO {
    Long getVideoSeq();
    String getVideo();
    String getVideoThumnail();
    String getTitle();
    String getContent();
    LocalDateTime getRegDate();
    LocalDateTime getUpdateDate();
    Long getMemberSeq();  // 추가: 소유자 정보

    @Value("#{@dateFormatter.formatDateTime(target.regDate)}")
    String getFormattedRegDate();

    @Value("#{@dateFormatter.formatDateTime(target.updateDate)}")
    String getFormattedUpdateDate();

}
