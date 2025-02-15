package io.goorm.youtube.repository.case1;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.goorm.youtube.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static io.goorm.youtube.domain.QVideo.video1;

@Repository
@RequiredArgsConstructor
public class VideoQueryRepository {

    private final JPAQueryFactory queryFactory;

    // 1. 특정 회원의 공개된 영상 목록 조회
    public List<Video> findPublicVideosByMember(Long memberSeq) {
        return queryFactory
                .selectFrom(video1)
                .where(
                        video1.memberSeq.eq(memberSeq),
                        video1.publishYn.eq(1),
                        video1.deleteYn.eq("N")
                )
                .orderBy(video1.regDate.desc())
                .fetch();
    }

    // 2. 제목 또는 내용으로 영상 검색
    public List<Video> searchVideos(String keyword) {
        return queryFactory
                .selectFrom(video1)
                .where(
                        video1.deleteYn.eq("N"),
                        video1.publishYn.eq(1),
                        video1.title.contains(keyword)
                                .or(video1.content.contains(keyword))
                )
                .orderBy(video1.regDate.desc())
                .fetch();
    }

    // 3. 특정 기간 내 등록된 영상 조회
    public List<Video> findVideosByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .selectFrom(video1)
                .where(
                        video1.deleteYn.eq("N"),
                        video1.regDate.between(startDate, endDate)
                )
                .orderBy(video1.regDate.desc())
                .fetch();
    }

    // 4. 페이지네이션이 적용된 영상 목록 조회
    public Page<Video> findVideosWithPagination(Pageable pageable) {
        List<Video> videos = queryFactory
                .selectFrom(video1)
                .where(video1.deleteYn.eq("N"))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(video1.regDate.desc())
                .fetch();

        Long total = queryFactory
                .select(video1.count())
                .from(video1)
                .where(video1.deleteYn.eq("N"))
                .fetchOne();

        return new PageImpl<>(videos, pageable, total);
    }

    // 5. 동적 조건 쿼리 예제
    public List<Video> findVideosByCondition(VideoSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        // 기본 조건
        builder.and(video1.deleteYn.eq("N"));

        // 제목 검색
        if (StringUtils.hasText(condition.getTitle())) {
            builder.and(video1.title.contains(condition.getTitle()));
        }

        // 공개 여부
        if (condition.getPublishYn() != null) {
            builder.and(video1.publishYn.eq(condition.getPublishYn()));
        }

        // 회원 검색
        if (condition.getMemberSeq() != null) {
            builder.and(video1.memberSeq.eq(condition.getMemberSeq()));
        }

        return queryFactory
                .selectFrom(video1)
                .where(builder)
                .orderBy(video1.regDate.desc())
                .fetch();
    }
}
