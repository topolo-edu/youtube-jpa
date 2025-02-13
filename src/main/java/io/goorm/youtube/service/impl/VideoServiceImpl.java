package io.goorm.youtube.service.impl;


import io.goorm.youtube.admin.VideoCreateDTO;
import io.goorm.youtube.admin.VideoMainDTO;
import io.goorm.youtube.admin.VideoResponseDTO;
import io.goorm.youtube.repository.VideoRepository;
import io.goorm.youtube.domain.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VideoServiceImpl  {


    private VideoRepository videoRepository;

    @Autowired
    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Transactional(readOnly = true)
    public List<VideoMainDTO> findIndex() {

        return videoRepository.findIndex();
    }

    @Transactional(readOnly = true)
    public Page<VideoMainDTO> findAll(Pageable pageable) {

        return videoRepository.findAllByDeleteYn("N", pageable);
    }

    @Transactional
    public VideoResponseDTO save(VideoCreateDTO videoCreateDTO) {

        Video video = new Video();
        BeanUtils.copyProperties(videoCreateDTO, video);

        Video savedVideo = videoRepository.save(video);

        return videoRepository.findVideoByVideoSeq(savedVideo.getVideoSeq())
                .orElseThrow(() -> new RuntimeException("등록한 비디오를 찾을 수 없습니다."));
    }



    @Transactional
    public void  update(Long id, VideoCreateDTO updateDto) {

        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 Video가 존재하지 않습니다."));

        // null이 아닌 값만 업데이트
        if (updateDto.getVideo() != null) {
            video.setVideo(updateDto.getVideo());
        }
        if (updateDto.getVideoThumnail() != null) {
            video.setVideoThumnail(updateDto.getVideoThumnail());
        }
        if (updateDto.getContent() != null) {
            video.setContent(updateDto.getContent());
        }
        if (updateDto.getTitle() != null) {
            video.setTitle(updateDto.getTitle());
        }

        // videoRepository.save(video); // 명시적으로 호출할 필요가 없음

    }

    @Transactional(readOnly = true)
    public VideoResponseDTO getVideoById(Long id) {
        return videoRepository.findVideoByVideoSeq(id)
                .orElseThrow(() -> new RuntimeException("해당 Video가 존재하지 않습니다."));
    }

    @Transactional
    public void  updatePublishYn(Long id) {

        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 Video가 존재하지 않습니다."));

        video.setPublishYn(video.getPublishYn() == 1 ? 0 : 1);

        // videoRepository.save(video); // 명시적으로 호출할 필요가 없음

    }

    @Transactional
    public void delete(Long id) {
        // 엔티티 존재 여부를 확인 후 삭제
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 Video가 존재하지 않습니다."));

        videoRepository.delete(video);
    }
}
