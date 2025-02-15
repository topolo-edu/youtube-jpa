package io.goorm.youtube.repository.case1;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoSearchCondition {
    private String title;
    private Integer publishYn;
    private Long memberSeq;
}
