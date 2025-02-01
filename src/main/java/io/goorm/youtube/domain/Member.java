package io.goorm.youtube.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long memberSeq;

    private String memberId;

    private String memberPw;

    private String memberNick;

    private String memberProfile;

    private String memberInfo;

    private String useYn;
    private String regDate;

    @OneToMany(mappedBy = "memberSeq", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos;

    @Transient
    private int videoCnt;


}
