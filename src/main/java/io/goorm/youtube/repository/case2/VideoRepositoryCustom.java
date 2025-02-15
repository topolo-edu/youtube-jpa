package io.goorm.youtube.repository.case2;

import io.goorm.youtube.domain.Video;
import io.goorm.youtube.repository.case1.VideoSearchCondition;

import java.util.List;

public interface VideoRepositoryCustom {

    List<Video> findVideosByCondition(VideoSearchCondition condition);
    List<Video> findPublicVideosByMember(Long memberSeq);
    List<Video> searchVideos(String keyword);  // 이 부분 추가
}
