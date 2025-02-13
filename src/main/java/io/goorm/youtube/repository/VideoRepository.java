package io.goorm.youtube.repository;

import io.goorm.youtube.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {


}
