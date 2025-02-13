package io.goorm.youtube.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MemberUpdateDTO {

    private Long memberSeq;

    private String memberId;

    private String memberNick;

    private String memberProfile;

    private String memberInfo;

}
