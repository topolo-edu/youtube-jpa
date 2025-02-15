package io.goorm.youtube.repository.case2.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.goorm.youtube.domain.Video;
import io.goorm.youtube.repository.case1.VideoSearchCondition;
import io.goorm.youtube.repository.case2.VideoRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static io.goorm.youtube.domain.QVideo.video1;

@Repository
public class VideoRepositoryImpl implements VideoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public VideoRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
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

    @Override
    public List<Video> findPublicVideosByMember(Long memberSeq) {
        return queryFactory
                .selectFrom(video1)
                .where(
                        video1.memberSeq.eq(memberSeq),
                        video1.publishYn.eq(1),
                        video1.deleteYn.eq("N")
                )
                .fetch();
    }

    @Override
    public List<Video> searchVideos(String keyword) {
        return queryFactory
                .selectFrom(video1)
                .where(
                        video1.deleteYn.eq("N"),
                        video1.publishYn.eq(1),
                        video1.title.contains(keyword)
                                .or(video1.content.contains(keyword))
                )
                .fetch();
    }
}