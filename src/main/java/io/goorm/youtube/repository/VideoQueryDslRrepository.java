package io.goorm.youtube.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.goorm.youtube.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static io.goorm.youtube.domain.QMember.member;
import static io.goorm.youtube.domain.QVideo.video;

@Repository
@RequiredArgsConstructor
public class VideoQueryDslRrepository {

    private final JPAQueryFactory queryFactory;

    // 시나리오 1: 기본적인 다중 조건 검색
    public List<Video> searchVideos(String title, String content, Integer publishYn) {
        return queryFactory
                .selectFrom(video)
                .where(
                        titleContains(title),
                        contentContains(content),
                        publishYnEquals(publishYn),
                        video.deleteYn.eq("N")
                )
                .orderBy(video.regDate.desc())
                .fetch();
    }

    // 시나리오 2: BooleanBuilder를 사용한 동적 쿼리
    public List<Video> searchVideosDynamic(String title, String content, Integer publishYn) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(title)) {
            builder.and(video.title.contains(title));
        }
        if (StringUtils.hasText(content)) {
            builder.and(video.content.contains(content));
        }
        if (publishYn != null) {
            builder.and(video.publishYn.eq(publishYn));
        }

        builder.and(video.deleteYn.eq("N"));

        return queryFactory
                .selectFrom(video)
                .where(builder)
                .orderBy(video.regDate.desc())
                .fetch();
    }

    // 시나리오 3: 날짜 범위와 함께 검색
    public List<Video> searchVideosByDateRange(
            String title,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return queryFactory
                .selectFrom(video)
                .where(
                        titleContains(title),
                        dateRange(startDate, endDate),
                        video.deleteYn.eq("N")
                )
                .orderBy(video.regDate.desc())
                .fetch();
    }

    // 시나리오 4: 회원 정보와 조인하여 검색
    public List<Video> searchVideosWithMember(String title, String memberNick) {
        return queryFactory
                .selectFrom(video)
                .join(member).on(video.memberSeq.eq(member.memberSeq))
                .where(
                        titleContains(title),
                        memberNickEquals(memberNick),
                        video.deleteYn.eq("N")
                )
                .orderBy(video.regDate.desc())
                .fetch();
    }

    // 분리된 조건문들
    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? video.title.contains(title) : null;
    }

    private BooleanExpression contentContains(String content) {
        return StringUtils.hasText(content) ? video.content.contains(content) : null;
    }

    private BooleanExpression publishYnEquals(Integer publishYn) {
        return publishYn != null ? video.publishYn.eq(publishYn) : null;
    }

    private BooleanExpression memberNickEquals(String memberNick) {
        return StringUtils.hasText(memberNick) ? member.memberNick.eq(memberNick) : null;
    }

    private BooleanExpression dateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return video.regDate.between(startDate, endDate);
    }
}
