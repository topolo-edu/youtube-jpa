package io.goorm.youtube.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import io.goorm.youtube.domain.Member;

@Getter
@Setter
@NoArgsConstructor
public class MemberCreateDTO {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 3, max = 20, message = "아이디는 3~20자 사이여야 합니다.")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    private String memberPw;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 30, message = "닉네임은 2~30자 사이여야 합니다.")
    private String memberNick;

    private MultipartFile memberProfileFile;

    @Size(max = 500, message = "회원 정보는 500자를 초과할 수 없습니다.")
    private String memberInfo;

    // DTO를 Entity로 변환하는 메서드
    public Member toEntity(String profilePath) {
        Member member = new Member();
        member.setMemberId(this.memberId);
        member.setMemberPw(this.memberPw);
        member.setMemberNick(this.memberNick);
        member.setMemberProfile(profilePath);
        member.setMemberInfo(this.memberInfo);
        member.setUseYn("Y");
        return member;
    }
}
