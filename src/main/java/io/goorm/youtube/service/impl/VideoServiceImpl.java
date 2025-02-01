package io.goorm.youtube.service.impl;


import io.goorm.youtube.domain.Member;
import io.goorm.youtube.repository.VideoRepository;
import io.goorm.youtube.domain.Video;
import io.goorm.youtube.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
@org.springframework.stereotype.Service
public class VideoServiceImpl implements VideoService {


    private VideoRepository videoRepository;

    @Autowired
    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public List<Video> findIndex() {

        return videoRepository.findAll();
    }

    public List<Video> findAll() {

        return videoRepository.findAll();
    }

    public Optional<Video> find(Long videoSeq) {

        return videoRepository.findById(videoSeq);
    }

    public Video save(Video video) {

        return videoRepository.save(video);
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

    }

}
