package io.goorm.youtube.controller;

import io.goorm.youtube.commom.util.FileUploadUtil;
import io.goorm.youtube.commom.util.SessionUtils;
import io.goorm.youtube.domain.Video;
import io.goorm.youtube.service.impl.VideoServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class VideoController {

    private final VideoServiceImpl videoService;

    @Autowired
    public VideoController(VideoServiceImpl videoService) {
        this.videoService = videoService;
    }

    //생성
    @PostMapping("/test")
    public ResponseEntity<?> create(@ModelAttribute Video video,
                         @RequestParam("videoFile") MultipartFile videoFile,
                         @RequestParam("videoThumnailFile") MultipartFile videoThumbnailFile) {

        try {

            //Long memberSeqBySession = SessionUtils.getMemberSeq(session);

            Long memberSeqBySession = 1L; // 셋션에서 가져왔다고 치고

            if (memberSeqBySession == null) {

                Map<String, Object> response = new HashMap<>();
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.badRequest().body(response);

            }


            // 업로드된 파일 처리
            String thumbnailPath = FileUploadUtil.uploadFile(videoThumbnailFile, "thumbnail");
            String videoPath = FileUploadUtil.uploadFile(videoFile, "vod");


            // 업로드된 파일 경로를 엔티티에 설정
            video.setVideoThumnail(thumbnailPath);
            video.setVideo(videoPath);


            videoService.save(memberSeqBySession,video);

            URI location = URI.create("/api/test");

            return ResponseEntity.created(location).build();

        } catch (Exception e) {
             return ResponseEntity.badRequest().body("비디오 생성에 실패했습니다." + e.toString());

        }
    }

    //생성
    @PostMapping("/test2")
    public ResponseEntity<?> create2(@ModelAttribute Video video,
                                    @RequestParam("videoFile") MultipartFile videoFile,
                                    @RequestParam("videoThumnailFile") MultipartFile videoThumbnailFile) {

        try {

            //Long memberSeqBySession = SessionUtils.getMemberSeq(session);

            Long memberSeqBySession = 1L; // 셋션에서 가져왔다고 치고

            if (memberSeqBySession == null) {

                Map<String, Object> response = new HashMap<>();
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.badRequest().body(response);

            }


            // 업로드된 파일 처리
            String thumbnailPath = FileUploadUtil.uploadFile(videoThumbnailFile, "thumbnail");
            String videoPath = FileUploadUtil.uploadFile(videoFile, "vod");


            // 업로드된 파일 경로를 엔티티에 설정
            video.setVideoThumnail(thumbnailPath);
            video.setVideo(videoPath);


            videoService.save2(memberSeqBySession,video);

            URI location = URI.create("/api/test2");

            return ResponseEntity.created(location).build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("비디오 생성에 실패했습니다." + e.toString());

        }
    }


    @GetMapping("/test3")
    public void test3(){
        videoService.testSelect();
    }

    @GetMapping("/test4")
    public void test4(){
        videoService.testSelect2();
    }

    @GetMapping("/test5")
    public void test5(){
        videoService.testSelect2();
    }

    @GetMapping("/test6")
    public void test6(){
       // videoService.getReferenceByIdTest();
        videoService.getReferenceByIdTest2();
    }

}
