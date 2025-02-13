package io.goorm.youtube.admin;


import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;


public interface VideoResponseDTO {

    Long getVideoSeq();
    String getVideo();
    String getVideoThumnail();
    String getTitle();
    String getContent();
    LocalDateTime getRegDate();

    @Value("#{@dateFormatter.formatDateTime(target.regDate)}")
    String getFormattedRegDate();

    @Value("#{@dateFormatter.formatDateTime(target.updateDate)}")
    String getFormattedUpdateDate();
}
