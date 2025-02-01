package io.goorm.youtube.service;


import io.goorm.youtube.domain.Video;


import java.util.List;
import java.util.Optional;


public interface VideoService {


    public List<Video> findIndex();

    public List<Video> findAll();

    public Optional<Video> find(Long videoSeq);

    public Video save(Video video);

    public Video update(Video video);

    public Video updatePublishYn(Long vidoeSeq);

    public Video updateDeleteYn(Long vidoeSeq);

}
