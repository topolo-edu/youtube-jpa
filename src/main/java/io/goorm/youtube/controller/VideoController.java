package io.goorm.youtube.controller;

import io.goorm.youtube.admin.VideoCreateDTO;
import io.goorm.youtube.admin.VideoMainDTO;
import io.goorm.youtube.commom.util.FileUploadUtil;
import io.goorm.youtube.domain.Video;

import io.goorm.youtube.service.impl.VideoServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class VideoController {

    private final VideoServiceImpl videoService;

    @Autowired
    public VideoController(VideoServiceImpl videoService) {
        this.videoService = videoService;
    }

    //리스트
    @GetMapping("/videos")
    public ResponseEntity<Page<VideoMainDTO>> list(
            @PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok(videoService.findAll(pageable));

    }

/*
    //뷰
    @GetMapping("/videos/{videoSeq}")
    public String  get(@PathVariable("videoSeq") Long videoSeq, Model model) {

        model.addAttribute("posts", videoService.find(videoSeq));
        model.addAttribute("title", "비디오-상세조회" );

        return "video/view";
    }

*/

    //생성
    @PostMapping("/videos")
    public ResponseEntity<?> create(@ModelAttribute VideoCreateDTO videoCreateDTO,
                         @RequestParam("videoFile") MultipartFile videoFile,
                         @RequestParam("videoThumnailFile") MultipartFile videoThumbnailFile) {

        try {

            // 업로드된 파일 처리
            String thumbnailPath = FileUploadUtil.uploadFile(videoThumbnailFile, "thumbnail");
            String videoPath = FileUploadUtil.uploadFile(videoFile, "vod");


            // 업로드된 파일 경로를 엔티티에 설정
            videoCreateDTO.setVideoThumnail(thumbnailPath);
            videoCreateDTO.setVideo(videoPath);


            VideoCreateDTO createDTO = videoService.save(videoCreateDTO);

            return ResponseEntity.ok(createDTO);

        } catch (Exception e) {
             return ResponseEntity.badRequest().body("실패");

        }
    }


/*
    //수정
    @PostMapping("/videos/{videoSeq}")
    public String  update(@ModelAttribute Video video, Model model, RedirectAttributes redirectAttributes) {

        videoService.update(video);

        redirectAttributes.addAttribute("videoSeq", video.getVideoSeq());
        redirectAttributes.addFlashAttribute("msg", "수정에 성공하였습니다.");

        return "redirect:/videos/{videoSeq}";

    }*/

}
