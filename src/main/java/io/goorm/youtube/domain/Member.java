package io.goorm.youtube.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

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
    private String regDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq")
    private List<Video> videos = new ArrayList<>();

}
