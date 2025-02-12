package io.goorm.youtube.domain;

import io.goorm.youtube.enums.DeleteStatus;
import io.goorm.youtube.enums.PublishStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@Entity
public class Video {

    public Video() {
        this.deleteYn = "N";
        this.publishYn = 0;
        this.member_seq = 10L;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoSeq;

    private String video;
    private String videoThumnail;

    private String title;
    private String content;

    private int publishYn;

    private String deleteYn;

    private Long member_seq;
    private String regDate;


}
