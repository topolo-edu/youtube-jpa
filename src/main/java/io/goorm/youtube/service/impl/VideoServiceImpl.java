package io.goorm.youtube.service.impl;


import io.goorm.youtube.domain.Member;
import io.goorm.youtube.domain.Video;
import io.goorm.youtube.dto.VideoCreateDto;
import io.goorm.youtube.dto.VideoResponseDto;
import io.goorm.youtube.dto.VideoResponseIndexDto;
import io.goorm.youtube.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class VideoServiceImpl {


    private VideoRepository videoRepository;

    @Autowired
    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public List<VideoResponseIndexDto> findIndex() {
        return videoRepository.findIndex();
    }

    public Page<VideoResponseDto> findAll(Pageable pageable) {
        return videoRepository.findAllByDeleteYn("N", pageable);
    }

    public VideoResponseDto save(VideoCreateDto videoCreateDto) {
        // 새로운 엔티티 생성 후 DTO의 값 복사
        Video video = new Video();

        BeanUtils.copyProperties(videoCreateDto, video);

/*        Member membTemp= new Member();

        membTemp.setMemberSeq(10L);
        video.setMember(membTemp);*/


        // 새 엔티티 등록 (영속화)
        Video savedVideo = videoRepository.save(video);

        // 등록된 엔티티 정보를 인터페이스 기반 Projection으로 조회하여 반환
        return videoRepository.findVideoResponseDtoByVideoSeq(savedVideo.getVideoSeq())
                .orElseThrow(() -> new RuntimeException("등록된 Video를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public VideoResponseDto getVideoById(Long id) {
        return videoRepository.findVideoResponseDtoByVideoSeq(id)
                .orElseThrow(() -> new RuntimeException("해당 Video가 존재하지 않습니다."));
    }

    @Transactional
    public VideoCreateDto  update(Long id, VideoCreateDto updateDto) {

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

        VideoCreateDto resultDto = new VideoCreateDto();
        BeanUtils.copyProperties(video, resultDto);

        return resultDto;
    }

    public void delete(Long id) {
        // 엔티티 존재 여부를 확인 후 삭제
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 Video가 존재하지 않습니다."));
        videoRepository.delete(video);
    }

}
