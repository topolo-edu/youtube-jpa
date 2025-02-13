package io.goorm.youtube.admin;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface MemberResponseDTO {

    Long getMemberSeq();
    String getMemberId();
    String getMemberNick();
    String getMemberProfile();
    String getMemberInfo();

    LocalDateTime getRegDate();

    @Value("#{@dateFormatter.formatDateTime(target.regDate)}")
    String getFormattedRegDate();

}
