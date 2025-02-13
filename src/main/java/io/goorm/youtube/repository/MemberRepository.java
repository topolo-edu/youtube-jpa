package io.goorm.youtube.repository;

import io.goorm.youtube.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByMemberId(String MemberId);

    boolean existsByMemberId(String memberId);
}
