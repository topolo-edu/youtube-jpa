package io.goorm.youtube.repository;

import io.goorm.youtube.dto.MemberResponseDTO;
import io.goorm.youtube.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByMemberId(String MemberId);

    boolean existsByMemberId(String memberId);

    @Query("SELECT m FROM Member m")
    Page<MemberResponseDTO> findListAll(Pageable pageable);

    Optional<MemberResponseDTO> findByMemberSeq(Long MemberSeq);
}
