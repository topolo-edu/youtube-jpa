package io.goorm.youtube.dto;

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

    // 민감한 정보는 제외 (비밀번호 등)

    // 추가: 계정 상태 정보
    String getUseYn();

    // 추가: 유틸리티 메서드
    @Value("#{target.useYn == 'Y'}")
    boolean isActive();


}
