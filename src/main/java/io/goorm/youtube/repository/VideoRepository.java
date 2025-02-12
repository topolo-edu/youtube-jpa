package io.goorm.youtube.repository;

import io.goorm.youtube.domain.Video;
import io.goorm.youtube.dto.VideoResponseDto;
import io.goorm.youtube.dto.VideoResponseIndexDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT v FROM Video v WHERE v.deleteYn = 'N' AND v.publishYn = 1 ORDER BY v.regDate DESC")
    List<VideoResponseIndexDto> findIndex();

    Page<VideoResponseDto> findAllByDeleteYn(String deleteYn, Pageable pageable);

    // 인터페이스 기반 Projection을 사용한 조회 메서드 (DTO 반환)
    Optional<VideoResponseDto> findVideoResponseDtoByVideoSeq(Long id);
}
