package io.goorm.youtube.admin;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class VideoCreateDTO {

    String video;
    String videoThumnail;
    String content;
    String title;

}
