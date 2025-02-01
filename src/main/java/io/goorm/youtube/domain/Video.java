package io.goorm.youtube.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "VIDEO")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long videoSeq;

    private String video;
    private String videoThumnail;

    private String title;
    private String content;

    private String channelName;

    private int publishYn;
    private String deleteYn;

    private String memberSeq;
    private String regDate;


}
