package io.goorm.youtube.service.impl;

import io.goorm.youtube.dto.MemberResponseDTO;
import io.goorm.youtube.dto.MemberUpdateDTO;
import io.goorm.youtube.commom.util.StringUtils;
import io.goorm.youtube.repository.MemberRepository;
import io.goorm.youtube.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class MemberServiceImpl  {



    private MemberRepository memberRepository;

    @Autowired
    public MemberServiceImpl(MemberRepository mmberRepository) {
        this.memberRepository = mmberRepository;
    }

    @Transactional(readOnly = true)
    public Page<MemberResponseDTO> findAll(Pageable pageable) {

        return memberRepository.findListAll(pageable);
    }

    @Transactional(readOnly = true)
    public MemberResponseDTO find(Long memberSeq) {

        return memberRepository.findByMemberSeq(memberSeq).orElseThrow(() -> new RuntimeException("해당 Member가 존재하지 않습니다."));

    }

    @Transactional(readOnly = true)
    public boolean existsById(String memberId) {

        return memberRepository.existsByMemberId(memberId);
    }

    @Transactional
    public Member save(Member member) {

        return memberRepository.save(member);
    }

    @Transactional
    public void update(Long memberSeq, MemberUpdateDTO memberUpdateDTO) {

        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new RuntimeException("해당 Member가  존재하지 않습니다."));

        // null이 아닌 값만 업데이트
        if (StringUtils.isNotBlank(memberUpdateDTO.getMemberId())) {
            member.setMemberId(memberUpdateDTO.getMemberId());
        }
        if (StringUtils.isNotBlank(memberUpdateDTO.getMemberNick())) {
            member.setMemberNick(memberUpdateDTO.getMemberNick());
        }
        if (StringUtils.isNotBlank(memberUpdateDTO.getMemberProfile())) {
            member.setMemberProfile(memberUpdateDTO.getMemberProfile());
        }
        if (StringUtils.isNotBlank(memberUpdateDTO.getMemberInfo())) {
            member.setMemberInfo(memberUpdateDTO.getMemberInfo());
        }

        //memberRepository.save(member);
    }

    @Transactional
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
