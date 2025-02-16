package io.goorm.youtube.dto;


import io.goorm.youtube.commom.util.FileUploadUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.util.HtmlUtils;

@Setter
@Getter
@NoArgsConstructor
public class VideoCreateDTO {
    @Pattern(regexp = "^[a-zA-Z0-9/\\\\:._-]+$", message = "비디오 경로에 허용되지 않는 문자가 포함되어 있습니다.")
    private String video;

    @Pattern(regexp = "^[a-zA-Z0-9/\\\\:._-]+$", message = "썸네일 경로에 허용되지 않는 문자가 포함되어 있습니다.")
    private String videoThumnail;

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 5000, message = "내용은 5000자를 초과할 수 없습니다.")
    private String content;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(min = 1, max = 200, message = "제목은 1~200자 사이여야 합니다.")
    private String title;

    private Long memberSeq;

    // XSS 방지를 위한 입력값 sanitize 메서드
    public void sanitize() {
        if (this.title != null) {
            this.title = HtmlUtils.htmlEscape(this.title);
        }
        if (this.content != null) {
            this.content = HtmlUtils.htmlEscape(this.content);
        }
    }
}
