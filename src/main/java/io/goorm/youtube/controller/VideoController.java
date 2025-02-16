package io.goorm.youtube.controller;

import io.goorm.youtube.dto.VideoCreateDTO;
import io.goorm.youtube.dto.VideoMainDTO;
import io.goorm.youtube.dto.VideoResponseDTO;
import io.goorm.youtube.security.CustomUserDetails;
import io.goorm.youtube.service.impl.VideoServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VideoController {

    private final VideoServiceImpl videoService;

    /**
     * 메인 페이지 비디오 목록
     */
    @GetMapping("")
    public ResponseEntity<List<VideoMainDTO>> getMainVideos() {
        return ResponseEntity.ok(videoService.findIndex());
    }

    /**
     * 내 비디오 목록 조회
     */
    @GetMapping("/me/videos")
    public ResponseEntity<Page<VideoMainDTO>> getMyVideos(
            @PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(videoService.findAll(userDetails.getMember().getMemberSeq(), pageable));
    }

    /**
     * 비디오 상세 조회
     */
    @PreAuthorize("@videoService.isOwner(#videoSeq, principal.member.memberSeq)")
    @GetMapping("/me/videos/{videoSeq}")
    public ResponseEntity<VideoResponseDTO> getVideo(
            @PathVariable Long videoSeq,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        log.info("Video access attempt: videoSeq={}, memberSeq={}",
                videoSeq, userDetails.getMember().getMemberSeq());


        VideoResponseDTO video = videoService.getVideoBySeq(videoSeq);

        return ResponseEntity.ok(video);
    }

    /**
     * 비디오 업로드
     */
    @PostMapping("/me/videos")
    public ResponseEntity<?> uploadVideo(
            @ModelAttribute VideoCreateDTO videoCreateDTO,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam("thumbnailFile") MultipartFile thumbnailFile,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {

            // 회원 정보 설정
            videoCreateDTO.setMemberSeq(userDetails.getMember().getMemberSeq());

            // XSS 방지
            //videoCreateDTO.sanitize();

            // 비디오 저장
            VideoResponseDTO savedVideo = videoService.save(videoCreateDTO, videoFile, thumbnailFile);

            return ResponseEntity
                    .created(URI.create("/api/me/videos/" + savedVideo.getVideoSeq()))
                    .body(savedVideo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("비디오 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "비디오 업로드에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 비디오 정보 수정
     */
    @PreAuthorize("@videoService.isOwner(#videoSeq, principal.member.memberSeq)")
    @PutMapping("/me/videos/{videoSeq}")
    public ResponseEntity<?> updateVideo(
            @PathVariable Long videoSeq,
            @Valid @ModelAttribute VideoCreateDTO videoUpdateDTO,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {

            // XSS 방지
            videoUpdateDTO.sanitize();

            // 비디오 수정
            videoService.update(videoSeq, videoUpdateDTO, videoFile, thumbnailFile);

            return ResponseEntity.ok()
                    .body(Map.of("message", "비디오가 성공적으로 수정되었습니다."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("비디오 수정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "비디오 수정에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 비디오 공개/비공개 설정
     */
    @PreAuthorize("@videoService.isOwner(#videoSeq, principal.member.memberSeq)")
    @PutMapping("/me/videos/{videoSeq}/publish")
    public ResponseEntity<?> toggleVideoPublish(
            @PathVariable Long videoSeq,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {

            videoService.updatePublishYn(videoSeq);

            return ResponseEntity.ok()
                    .body(Map.of("message", "비디오 공개 상태가 변경되었습니다."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("비디오 공개 상태 변경 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "비디오 공개 상태 변경에 실패했습니다."));
        }
    }

    /**
     * 비디오 삭제
     */
    @PreAuthorize("@videoService.isOwner(#videoSeq, principal.member.memberSeq)")
    @DeleteMapping("/me/videos/{videoSeq}")
    public ResponseEntity<?> deleteVideo(
            @PathVariable Long videoSeq,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {

            videoService.delete(videoSeq);

            return ResponseEntity.ok()
                    .body(Map.of("message", "비디오가 성공적으로 삭제되었습니다."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("비디오 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "비디오 삭제에 실패했습니다."));
        }
    }
}