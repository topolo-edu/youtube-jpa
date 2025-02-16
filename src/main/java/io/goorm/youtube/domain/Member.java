package io.goorm.youtube.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberSeq;

    private String memberId;

    private String memberPw;

    private String memberNick;

    private String memberProfile;

    private String memberInfo;

    private String useYn;

    @Column(name = "reg_date", updatable = false)
    @CreationTimestamp  // 엔티티 생성 시 자동으로 현재 시간 설정
    private LocalDateTime regDate;

    @OneToMany
    @JoinColumn(name = "member_seq")
    private List<Video> videos = new ArrayList<>();

}
