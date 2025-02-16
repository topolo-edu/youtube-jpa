package io.goorm.youtube.controller;

import io.goorm.youtube.commom.util.FileUploadUtil;
import io.goorm.youtube.dto.VideoCreateDto;
import io.goorm.youtube.dto.VideoResponseDto;
import io.goorm.youtube.service.impl.VideoServiceImpl;
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
    @GetMapping("/me/videos")
    public ResponseEntity<Page<VideoResponseDto>> list(
            @PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok(videoService.findAll(pageable));

    }

    //생성
    @PostMapping("/me/videos")
    public ResponseEntity<?> create( @ModelAttribute VideoCreateDto videoCreateDto,
                          @RequestParam("videoFile") MultipartFile videoFile,
                          @RequestParam("videoThumnailFile") MultipartFile videoThumbnailFile) {


        try {
            // 업로드된 파일 처리
            String thumbnailPath = FileUploadUtil.uploadFile(videoThumbnailFile, "thumbnail");
            String videoPath = FileUploadUtil.uploadFile(videoFile, "vod");

            // 업로드된 파일 경로를 엔티티에 설정
            videoCreateDto.setVideo(videoPath);
            videoCreateDto.setVideoThumnail(thumbnailPath);

            VideoResponseDto createdvideo = videoService.save(videoCreateDto);

            return ResponseEntity.created(URI.create("/api/videos/" + createdvideo.getVideoSeq()))
                    .body(createdvideo);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body("비디오 저장에 실패했습니다.");

        }
    }

    //수정
    @PutMapping("/me/videos/{videoSeq}")
    public ResponseEntity<?> update( @PathVariable("videoSeq") Long videoSeq,
                                    @ModelAttribute VideoCreateDto videoUpdateDto,
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


            VideoCreateDto updatedvideo = videoService.update(videoSeq,videoUpdateDto);

            return ResponseEntity.created(URI.create("/api/videos/" + videoSeq))
                    .body(updatedvideo);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body("비디오 수정에 실패했습니다.");

        }
    }

    //뷰
    @GetMapping("/me/videos/{videoSeq}")
    public ResponseEntity<VideoResponseDto>  get(@PathVariable("videoSeq") Long videoSeq) {

        return ResponseEntity.ok(videoService.getVideoById(videoSeq));
    }

    //뷰
    @DeleteMapping("/me/videos/{videoSeq}")
    public ResponseEntity<?>  delete(@PathVariable("videoSeq") Long videoSeq) {

        videoService.delete(videoSeq);

        return ResponseEntity.ok(videoSeq);
    }



}
