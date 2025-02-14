package io.goorm.youtube.controller;

import io.goorm.youtube.admin.VideoCreateDTO;
import io.goorm.youtube.admin.VideoMainDTO;
import io.goorm.youtube.admin.VideoResponseDTO;
import io.goorm.youtube.commom.util.FileUploadUtil;
import io.goorm.youtube.commom.util.SessionUtils;
import io.goorm.youtube.service.impl.VideoServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    //본인리스트
    @GetMapping("/me/videos")
    public ResponseEntity<?> list(
            @PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable, HttpSession session
    ) {

        Long memberSeqBySession = SessionUtils.getMemberSeq(session);

        if (memberSeqBySession == null) {

            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.badRequest().body(response);

        }

        return ResponseEntity.ok(videoService.findAll(memberSeqBySession,pageable));

    }

    //본인동영상 보기
    @GetMapping("/me/videos/{videoSeq}")
    public ResponseEntity<VideoResponseDTO>  get(@PathVariable("videoSeq") Long videoSeq) {

        return ResponseEntity.ok(videoService.getVideoById(videoSeq));
    }


    //생성
    @PostMapping("/me/videos")
    public ResponseEntity<?> create(@ModelAttribute VideoCreateDTO videoCreateDTO,
                         @RequestParam("videoFile") MultipartFile videoFile,
                         @RequestParam("videoThumnailFile") MultipartFile videoThumbnailFile,
                                    HttpSession session) {

        try {

            Long memberSeqBySession = SessionUtils.getMemberSeq(session);

            if (memberSeqBySession == null) {

                Map<String, Object> response = new HashMap<>();
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.badRequest().body(response);

            }

            videoCreateDTO.setMemberSeq(memberSeqBySession);

            // 업로드된 파일 처리
            String thumbnailPath = FileUploadUtil.uploadFile(videoThumbnailFile, "thumbnail");
            String videoPath = FileUploadUtil.uploadFile(videoFile, "vod");


            // 업로드된 파일 경로를 엔티티에 설정
            videoCreateDTO.setVideoThumnail(thumbnailPath);
            videoCreateDTO.setVideo(videoPath);


            VideoResponseDTO createDTO = videoService.save(videoCreateDTO);

            URI location = URI.create("/api/me/videos");

            return ResponseEntity.created(location).build();

        } catch (Exception e) {
             return ResponseEntity.badRequest().body("비디오 생성에 실패했습니다." + e.toString());

        }
    }

    //본인 동영상 수정
    @PutMapping("/me/videos/{videoSeq}")
    public ResponseEntity<?> update( @PathVariable("videoSeq") Long videoSeq,
                                     @ModelAttribute VideoCreateDTO videoUpdateDto,
                                     @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
                                     @RequestParam(value = "videoThumnailFile", required = false) MultipartFile videoThumbnailFile) {


        try {
            // 업로드된 파일 처리
            if (videoThumbnailFile  != null  && !videoThumbnailFile.isEmpty()) {
                String thumbnailPath = FileUploadUtil.uploadFile(videoThumbnailFile, "thumbnail");
                videoUpdateDto.setVideo(thumbnailPath);
            }
            if (videoFile != null  && !videoFile.isEmpty()) {
                String videoPath = FileUploadUtil.uploadFile(videoFile, "vod");
                videoUpdateDto.setVideoThumnail(videoPath);
            }


            videoService.update(videoSeq,videoUpdateDto);

            return ResponseEntity.ok(videoSeq);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body("비디오 수정에 실패했습니다." + e.toString());

        }
    }

    @PutMapping("/me/videos/{videoSeq}/publish")
    public ResponseEntity<?> update( @PathVariable("videoSeq") Long videoSeq) {

        try {
            videoService.updatePublishYn(videoSeq);

            return ResponseEntity.ok(videoSeq);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body("비디오 상태 변경에 실패했습니다." + e.toString());

        }

    }


    //본인동영상삭제
    @DeleteMapping("/me/videos/{videoSeq}")
    public ResponseEntity<?>  delete(@PathVariable("videoSeq") Long videoSeq) {

        try {
            videoService.delete(videoSeq);

            return ResponseEntity.ok(videoSeq);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body("비디오 상태 삭제에 실패했습니다." + e.toString());

        }
    }

}
