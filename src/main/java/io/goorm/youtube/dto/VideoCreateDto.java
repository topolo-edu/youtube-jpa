package io.goorm.youtube.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VideoCreateDto {

    String video;
    String videoThumnail;
    String content;
    String title;

}
