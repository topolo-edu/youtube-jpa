package io.goorm.youtube.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Video {

    public Video() {
        this.publishYn = 0;
        this.deleteYn = "N";
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

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime regDate;

    @Column(nullable = true)
    private LocalDateTime updateDate;


    @PreUpdate
    public void onPreUpdate() {
        this.updateDate = LocalDateTime.now();
    }

}
