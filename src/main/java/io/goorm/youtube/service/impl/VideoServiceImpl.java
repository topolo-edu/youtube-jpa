package io.goorm.youtube.service.impl;


import io.goorm.youtube.domain.Member;
import io.goorm.youtube.repository.MemberRepository;
import io.goorm.youtube.repository.VideoRepository;
import io.goorm.youtube.domain.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VideoServiceImpl  {


    private VideoRepository videoRepository;
    private MemberRepository memberRepository;

    @Autowired
    public VideoServiceImpl(VideoRepository videoRepository,MemberRepository memberRepository) {
        this.videoRepository = videoRepository;
        this.memberRepository = memberRepository;
    }


    public Video save(Long memberSeq, Video video) {

        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new RuntimeException("등록한 비디오를 찾을 수 없습니다."));

        member.addVideo(video);

        memberRepository.save(member);

        return video;
    }

    public Video save2(Long memberSeq, Video video) {
        Member member = memberRepository.getReferenceById(memberSeq);  // findById() 대신 getReferenceById() 사용
        member.addVideo(video);

        return videoRepository.save(video);  // member.save() 대신 video.save()만 호출
    }


    public void testSelect(){
        Long member_no = 1L;
        Optional<Member> result = memberRepository.findById(member_no);
        System.out.println("=============================");
        if(result.isPresent()){
            Member member = result.get();
            System.out.println(member);
        }
    }


    @Transactional
    public void testSelect2(){
        Long member_no = 1L;
        Member result_get = memberRepository.getReferenceById(member_no);
        System.out.println("=============================");
        System.out.println(result_get);
    }

    @Transactional
    public void testSelect3(){
        Long member_no = 1L;
        Member result_get = memberRepository.getReferenceById(member_no);
        System.out.println("=============================");

        System.out.println(result_get.getMemberSeq());
    }

    @Transactional
    public void getReferenceByIdTest() {
        System.out.println("=== Before getReferenceById ===");

        Member member = memberRepository.getReferenceById(1L);

        //Member member = memberRepository.findById(1L).orElseThrow(() -> new RuntimeException("등록한 비디오를 찾을 수 없습니다."));

        System.out.println("=== After getReferenceById ===");
        System.out.println("member class: " + member.getClass().getName());

        System.out.println("=== Before accessing member property ===");

        String memberId = member.getMemberId();

        System.out.println("=== After accessing member property ===");

    }

    @Transactional
    public void getReferenceByIdTest2() {

        System.out.println("=== Before findById ===");

        Member member2 = memberRepository.findById(1L).orElseThrow(() -> new RuntimeException("등록한 비디오를 찾을 수 없습니다."));

        System.out.println("=== After findById ===");
        System.out.println("member class: " + member2.getClass().getName());

        System.out.println("=== Before accessing member2 property ===");

        String memberId2 = member2.getMemberId();

        System.out.println("=== After accessing member2 property ===");
    }

}
