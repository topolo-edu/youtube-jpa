package io.goorm.youtube.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;


@Setter
@Getter
@Entity
public class Video {

    public Video() {
        this.publishYn = 0;
        this.deleteYn = "N";

        this.member_seq = 5L;
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
