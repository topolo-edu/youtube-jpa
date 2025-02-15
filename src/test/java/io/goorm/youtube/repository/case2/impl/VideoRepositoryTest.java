package io.goorm.youtube.repository.case2.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.goorm.youtube.commom.util.FileUploadUtil;
import io.goorm.youtube.config.TestConfig;
import io.goorm.youtube.domain.Member;
import io.goorm.youtube.domain.Video;
import io.goorm.youtube.repository.case1.VideoSearchCondition;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@ImportAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
})
public class VideoRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JPAQueryFactory queryFactory;

    private VideoRepositoryImpl videoRepository;
    private Member testMember;  // 테스트용 멤버 저장

    @Autowired
    private FileUploadUtil fileUploadUtil;  // FileUploadUtil 주입

    @BeforeEach
    void setUp() {
        fileUploadUtil.setUploadDir("src/test/resources/uploads");  // 직접 설정
        videoRepository = new VideoRepositoryImpl(queryFactory);

        // 테스트용 멤버 생성
        testMember = createMember("testUser", "password", "테스터");
    }

    // 멤버 생성 헬퍼 메서드
    private Member createMember(String id, String password, String nickname) {
        Member member = new Member();
        member.setMemberId(id);
        member.setMemberPw(password);
        member.setMemberNick(nickname);
        member.setUseYn("Y");
        entityManager.persist(member);
        return member;
    }

    // 비디오 생성 헬퍼 메서드
    private Video createVideo(String title, String content, Long memberSeq, int publishYn) {
        Video video = new Video();
        video.setTitle(title);
        video.setContent(content);
        video.setMemberSeq(memberSeq);
        video.setPublishYn(publishYn);
        video.setDeleteYn("N");  // 명시적으로 설정
        entityManager.persist(video);
        return video;
    }


    @Test
    void findVideosByCondition() {

        // given
        createVideo("테스트 비디오1", "테스트 내용1", testMember.getMemberSeq(), 1);
        createVideo("테스트 비디오2", "테스트 내용2", testMember.getMemberSeq(), 0);
        entityManager.flush();

        // 디버깅을 위한 출력
        List<Video> allVideos = entityManager
                .createQuery("SELECT v FROM Video v", Video.class)
                .getResultList();
        System.out.println("All Videos in DB: " + allVideos.size());
        allVideos.forEach(v -> {
            System.out.println("Video: " + v.getTitle()
                    + ", publishYn: " + v.getPublishYn()
                    + ", deleteYn: " + v.getDeleteYn()
                    + ", memberSeq: " + v.getMemberSeq());
        });

        VideoSearchCondition condition = new VideoSearchCondition();
        condition.setPublishYn(1);
        condition.setMemberSeq(testMember.getMemberSeq());

        // when
        List<Video> videos = videoRepository.findVideosByCondition(condition);

        // then
        assertThat(videos).hasSize(1);
        assertThat(videos.get(0).getTitle()).isEqualTo("테스트 비디오1");
    }

    @Test
    void findPublicVideosByMember() {
        // given
        // Member 생성 및 저장
        createVideo("공개 비디오", "공개 내용", testMember.getMemberSeq(), 1);
        createVideo("비공개 비디오", "비공개 내용", testMember.getMemberSeq(), 0);
        entityManager.flush();

        // when
        List<Video> publicVideos = videoRepository.findPublicVideosByMember(1L);

        // then
        assertThat(publicVideos).hasSize(1);
        assertThat(publicVideos.get(0).getTitle()).isEqualTo("공개 비디오");
    }

    @Test
    void searchVideos() {
        // given
        createVideo("스프링 강좌", "스프링 부트 내용", testMember.getMemberSeq(), 1);
        createVideo("JPA 강좌", "하이버네이트 내용", testMember.getMemberSeq(), 1);
        entityManager.flush();

        // when
        List<Video> videos = videoRepository.searchVideos("스프링");

        // then
        assertThat(videos).hasSize(1);
        assertThat(videos.get(0).getTitle()).contains("스프링");
    }
}
