package io.goorm.youtube.repository;


import io.goorm.youtube.domain.Member;
import io.goorm.youtube.domain.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class VideoQueryRepositoryTest {

    @Autowired
    VideoQueryDslRrepository videoQueryRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 생성
        Member member = new Member();
        member.setMemberId("test");
        member.setMemberNick("테스터");
        em.persist(member);

        // 테스트용 비디오들 생성
        for(int i = 1; i <= 5; i++) {
            Video video = new Video();
            video.setTitle("테스트 비디오 " + i);
            video.setContent("테스트 내용 " + i);
            video.setPublishYn(i % 2);  // 0 또는 1
            video.setMemberSeq(member.getMemberSeq());
            em.persist(video);
        }

        em.flush();
        em.clear();
    }

    @Test
    void 기본_검색_테스트() {
        // when
        List<Video> videos = videoQueryRepository.searchVideos(
                "테스트 비디오",
                "테스트 내용",
                1
        );

        // then
        assertThat(videos).isNotEmpty();
        assertThat(videos).allMatch(v ->
                v.getTitle().contains("테스트 비디오") &&
                        v.getContent().contains("테스트 내용") &&
                        v.getPublishYn() == 1
        );
    }

    @Test
    void 동적쿼리_검색_테스트() {
        // when
        List<Video> videos = videoQueryRepository.searchVideosDynamic(
                "테스트 비디오",
                null,  // content 조건 제외
                1
        );

        // then
        assertThat(videos).isNotEmpty();
        assertThat(videos).allMatch(v ->
                v.getTitle().contains("테스트 비디오") &&
                        v.getPublishYn() == 1
        );
    }

    @Test
    void 날짜범위_검색_테스트() {
        // given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        // when
        List<Video> videos = videoQueryRepository.searchVideosByDateRange(
                "테스트 비디오",
                startDate,
                endDate
        );

        // then
        assertThat(videos).isNotEmpty();
        assertThat(videos).allMatch(v ->
                v.getTitle().contains("테스트 비디오") &&
                        v.getRegDate().isAfter(startDate) &&
                        v.getRegDate().isBefore(endDate)
        );
    }

    @Test
    void 회원정보_조인_검색_테스트() {
        // when
        List<Video> videos = videoQueryRepository.searchVideosWithMember(
                "테스트 비디오",
                "테스터"
        );

        // then
        assertThat(videos).isNotEmpty();
        assertThat(videos).allMatch(v ->
                v.getTitle().contains("테스트 비디오")
        );
    }
}
