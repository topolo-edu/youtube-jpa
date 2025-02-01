package io.goorm.youtube.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "ADMIN")
public class Admin  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long adminSeq;

    private String adminId;
    private String adminPw;
    private String adminName;
    private String useYn;

    private String regSeq;
    private String regName;
    private String regDate;
}
