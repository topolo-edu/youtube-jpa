package io.goorm.youtube.service.impl;

import io.goorm.youtube.repository.MemberRepository;
import io.goorm.youtube.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MemberServiceImpl  {



    private MemberRepository memberRepository;

    @Autowired
    public MemberServiceImpl(MemberRepository mmberRepository) {
        this.memberRepository = mmberRepository;
    }


    public List<Member> findAll() {

        return memberRepository.findAll();
    }

    public Member login(Member member) {

        return memberRepository.findByMemberId(member.getMemberId());
    }

    public Optional<Member> find(Long memberSeq) {

        return memberRepository.findById(memberSeq);
    }

    public boolean existsById(String memberId) {

        return memberRepository.existsByMemberId(memberId);
    }

    public Member save(Member member) {

        return memberRepository.save(member);
    }

    public Member update(Member member) {

        return memberRepository.save(member);
    }

    public Member updateUseYn(Long memberSeq) {

        Member existingMember = memberRepository.findById(memberSeq).orElseThrow(() -> new RuntimeException("Admin not found"));

        /*Optional<Member> optionalMember = memberRepository.findById(memberSeq);

        if (optionalMember.isPresent()) {
            Member existingMember = optionalMember.get();
            // 필요한 로직 수행
        } else {
            throw new RuntimeException("Member not found");
        }*/


        existingMember.setUseYn(existingMember != null && existingMember.getUseYn().equals("N") ? "Y" : "N");

        return memberRepository.save(existingMember);

    }

}
