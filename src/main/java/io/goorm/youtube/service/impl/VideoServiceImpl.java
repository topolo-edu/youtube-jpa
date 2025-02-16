package io.goorm.youtube.service.impl;

import io.goorm.youtube.dto.VideoCreateDTO;
import io.goorm.youtube.dto.VideoMainDTO;
import io.goorm.youtube.dto.VideoResponseDTO;
import io.goorm.youtube.commom.util.FileUploadUtil;
import io.goorm.youtube.domain.Video;
import io.goorm.youtube.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoServiceImpl {

    private final VideoRepository videoRepository;
    private final FileUploadUtil fileUploadUtil;

    /**
     * 메인 페이지용 비디오 목록 조회
     */
    public List<VideoMainDTO> findIndex() {
        return videoRepository.findIndex();
    }

    /**
     * 사용자별 비디오 목록 조회 (페이징)
     */
    public Page<VideoMainDTO> findAll(Long memberSeq, Pageable pageable) {
        return videoRepository.findAllByMemberSeqAndDeleteYn(memberSeq, "N", pageable);
    }

    /**
     * 비디오 단건 조회
     */
    public VideoResponseDTO getVideoBySeq(Long videoSeq) {
        return videoRepository.findVideoByVideoSeq(videoSeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 비디오가 존재하지 않습니다. ID: " + videoSeq));
    }

    /**
     * 비디오 업로드 및 저장
     */
    @Transactional
    public VideoResponseDTO save(VideoCreateDTO videoCreateDTO, MultipartFile videoFile, MultipartFile thumbnailFile) {
        try {
            // 파일 업로드
            String videoPath = fileUploadUtil.uploadFile(videoFile, "video");
            String thumbnailPath = fileUploadUtil.uploadFile(thumbnailFile, "thumbnail");

            // 엔티티 생성 및 저장
            Video video = new Video();
            video.setVideo(videoPath);
            video.setVideoThumnail(thumbnailPath);
            video.setTitle(videoCreateDTO.getTitle());
            video.setContent(videoCreateDTO.getContent());
            video.setMemberSeq(videoCreateDTO.getMemberSeq());
            video.setPublishYn(0);  // 기본값: 비공개
            video.setDeleteYn("N"); // 기본값: 삭제되지 않음

            Video savedVideo = videoRepository.save(video);

            return videoRepository.findVideoByVideoSeq(savedVideo.getVideoSeq())
                    .orElseThrow(() -> new RuntimeException("비디오 저장 후 조회 실패"));

        } catch (Exception e) {
            log.error("비디오 저장 중 오류 발생", e);
            throw new RuntimeException("비디오 저장에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 비디오 정보 수정
     */
    @Transactional
    public void update(Long videoSeq, VideoCreateDTO updateDto,
                       MultipartFile videoFile, MultipartFile thumbnailFile) {
        Video video = videoRepository.findById(videoSeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 비디오가 존재하지 않습니다."));

        try {
            // 새로운 비디오 파일이 업로드된 경우
            if (videoFile != null && !videoFile.isEmpty()) {
                String videoPath = fileUploadUtil.uploadFile(videoFile, "video");
                // 기존 파일 삭제
                fileUploadUtil.deleteFile(video.getVideo());
                video.setVideo(videoPath);
            }

            // 새로운 썸네일이 업로드된 경우
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String thumbnailPath = fileUploadUtil.uploadFile(thumbnailFile, "thumbnail");
                // 기존 파일 삭제
                fileUploadUtil.deleteFile(video.getVideoThumnail());
                video.setVideoThumnail(thumbnailPath);
            }

            // 나머지 정보 업데이트
            if (updateDto.getTitle() != null) {
                video.setTitle(updateDto.getTitle());
            }
            if (updateDto.getContent() != null) {
                video.setContent(updateDto.getContent());
            }

        } catch (Exception e) {
            log.error("비디오 수정 중 오류 발생", e);
            throw new RuntimeException("비디오 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 비디오 공개/비공개 상태 변경
     */
    @Transactional
    public void updatePublishYn(Long videoSeq) {
        Video video = videoRepository.findById(videoSeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 비디오가 존재하지 않습니다."));

        video.setPublishYn(video.getPublishYn() == 1 ? 0 : 1);
    }

    /**
     * 비디오 삭제
     */
    @Transactional
    public void delete(Long videoSeq) {
        Video video = videoRepository.findById(videoSeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 비디오가 존재하지 않습니다."));

        try {
            // 파일 삭제
            fileUploadUtil.deleteFile(video.getVideo());
            fileUploadUtil.deleteFile(video.getVideoThumnail());

            // 소프트 삭제 처리
            video.setDeleteYn("Y");
        } catch (Exception e) {
            log.error("비디오 삭제 중 오류 발생", e);
            throw new RuntimeException("비디오 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 회원의 비디오 존재 여부 확인
     */
    public boolean existsByMemberSeq(Long memberSeq) {
        return videoRepository.existsByMemberSeqAndDeleteYn(memberSeq, "N");
    }

    /**
     * 비디오 소유자 확인
     */
    public boolean isOwner(Long videoSeq, Long memberSeq) {
        return videoRepository.findById(videoSeq)
                .map(video -> video.getMemberSeq().equals(memberSeq))
                .orElse(false);
    }
}