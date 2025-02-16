package io.goorm.youtube.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.util.HtmlUtils;

@Setter
@Getter
@NoArgsConstructor
public class MemberUpdateDTO {
    @NotNull(message = "회원 번호는 필수입니다.")
    private Long memberSeq;

    @Size(min = 3, max = 20, message = "아이디는 3~20자 사이여야 합니다.")
    private String memberId;

    @Size(min = 2, max = 30, message = "닉네임은 2~30자 사이여야 합니다.")
    private String memberNick;

    @Pattern(regexp = "^[a-zA-Z0-9/\\\\:._-]+$", message = "프로필 경로에 허용되지 않는 문자가 포함되어 있습니다.")
    private String memberProfile;

    @Size(max = 500, message = "회원 정보는 500자를 초과할 수 없습니다.")
    private String memberInfo;

    // 추가: 입력값 sanitize 메서드
    public void sanitize() {
        if (this.memberNick != null) {
            this.memberNick = HtmlUtils.htmlEscape(this.memberNick);
        }
        if (this.memberInfo != null) {
            this.memberInfo = HtmlUtils.htmlEscape(this.memberInfo);
        }
    }
}
