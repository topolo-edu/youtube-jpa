package io.goorm.youtube.service.impl;


import io.goorm.youtube.admin.VideoCreateDTO;
import io.goorm.youtube.admin.VideoMainDTO;
import io.goorm.youtube.repository.VideoRepository;
import io.goorm.youtube.domain.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public List<VideoMainDTO> findIndex() {

        return videoRepository.findIndex();
    }


    public Page<VideoMainDTO> findAll(Pageable pageable) {

        return videoRepository.findAllByDeleteYn("N", pageable);
    }


    public VideoCreateDTO save(VideoCreateDTO videoCreateDTO) {

        Video video = new Video();
        BeanUtils.copyProperties(videoCreateDTO, video);

        Video savedVideo = videoRepository.save(video);

        return videoRepository.findVideoByVideoSeq(savedVideo.getVideoSeq())
                .orElseThrow(() -> new RuntimeException("등록한 비디오를 찾을 수 없습니다."));
    }


    /*

    public Optional<Video> find(Long videoSeq) {

        return videoRepository.findById(videoSeq);
    }



    public Video update(Video video) {

        Video existingVideo = videoRepository.findById(video.getVideoSeq()).orElseThrow();

        return videoRepository.save(existingVideo);

    }

    public Video updatePublishYn(Long vidoeSeq) {

        Video existingVideo = videoRepository.findById(vidoeSeq).orElseThrow(() -> new RuntimeException("Admin not found"));

        if (existingVideo != null && existingVideo.getPublishYn() == 1) {
            existingVideo.setPublishYn(0);
        } else {
            existingVideo.setPublishYn(1);
        }

        return videoRepository.save(existingVideo);

    }

    public Video updateDeleteYn(Long vidoeSeq) {

        Video existingVideo = videoRepository.findById(vidoeSeq).orElseThrow(() -> new RuntimeException("Admin not found"));

        if (existingVideo != null && existingVideo.getDeleteYn().equals("N")) {
            existingVideo.setDeleteYn("Y");
        } else {
            existingVideo.setDeleteYn("N");
        }

        return videoRepository.save(existingVideo);

    }*/

}
