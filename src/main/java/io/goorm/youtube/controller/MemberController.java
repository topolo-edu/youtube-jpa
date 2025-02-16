package io.goorm.youtube.controller;

import io.goorm.youtube.dto.MemberResponseDTO;
import io.goorm.youtube.dto.MemberUpdateDTO;
import io.goorm.youtube.commom.util.FileUploadUtil;
import io.goorm.youtube.domain.Member;
import io.goorm.youtube.security.CustomUserDetails;
import io.goorm.youtube.service.impl.MemberServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberServiceImpl memberService;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadUtil fileUploadUtil;

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity<?> join(
            @Valid @ModelAttribute Member member,
            @RequestParam(value = "memberProfileFile", required = false) MultipartFile profileFile
    ) {
        Map<String, Object> response = new HashMap<>();

        // 아이디 중복 검사
        if (memberService.existsById(member.getMemberId())) {
            response.put("message", "아이디가 중복 되었습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 프로필 이미지 처리
            if (profileFile != null && !profileFile.isEmpty()) {
                String profilePath = fileUploadUtil.uploadFile(profileFile, "profile");
                member.setMemberProfile(profilePath);
            }

            // 비밀번호 암호화
            member.setMemberPw(passwordEncoder.encode(member.getMemberPw()));
            member.setUseYn("Y");  // 기본값 설정

            // 회원 저장
            memberService.save(member);

            return ResponseEntity.created(URI.create("/api/join")).build();

        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("회원가입 실패", e);
            response.put("message", "회원가입에 실패하였습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/members")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // 현재 인증된 사용자의 memberId 추출
            Long memberSeq = userDetails.getMember().getMemberSeq();

            // 프로필 조회 서비스 호출
            MemberResponseDTO profileInfo = memberService.find(memberSeq);

            // 성공 응답
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "프로필 조회 성공",
                            "data", profileInfo
                    )
            );
        } catch (Exception e) {
            // 오류 응답
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "success", false,
                            "message", "프로필 조회 실패: " + e.getMessage()
                    )
            );
        }
    }

    // 2. 로그인 - Spring Security가 처리하므로 메서드 제거
    // 기존 /login 엔드포인트는 SecurityConfig에서 처리

    // 3. 로그아웃 - Spring Security가 처리하므로 메서드 제거
    // 기존 /logout 엔드포인트는 SecurityConfig에서 처리

    /**
     * 프로필 수정
     */
    @PutMapping("/members")
    public ResponseEntity<?> update(
            @Valid @ModelAttribute MemberUpdateDTO memberUpdateDTO,
            @RequestParam(value = "memberProfileFile", required = false) MultipartFile profileFile,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 권한 확인
        if (!userDetails.getMember().getMemberSeq().equals(memberUpdateDTO.getMemberSeq())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "본인의 정보가 아닙니다."));
        }

        try {
            // 프로필 이미지 처리
            if (profileFile != null && !profileFile.isEmpty()) {
                String profilePath = fileUploadUtil.uploadFile(profileFile, "profile");
                memberUpdateDTO.setMemberProfile(profilePath);

                // 기존 프로필 이미지 삭제
                MemberResponseDTO currentMember = memberService.find(memberUpdateDTO.getMemberSeq());

                if (currentMember.getMemberProfile() != null) {
                    fileUploadUtil.deleteFile(currentMember.getMemberProfile());
                }
            }

            memberService.update(memberUpdateDTO.getMemberSeq(), memberUpdateDTO);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("프로필 수정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "프로필 수정에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("/members/password")
    public ResponseEntity<?> updatePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(currentPassword, userDetails.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));
            }

            // 새 비밀번호 암호화 및 업데이트
            Member member = userDetails.getMember();
            member.setMemberPw(passwordEncoder.encode(newPassword));
            memberService.save(member);

            return ResponseEntity.ok()
                    .body(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));

        } catch (Exception e) {
            log.error("비밀번호 변경 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "비밀번호 변경에 실패했습니다."));
        }
    }

    /**
     * 회원 탈퇴 (비활성화)
     */
    @PutMapping("/members/deactivate")
    public ResponseEntity<?> deactivateMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Member member = userDetails.getMember();
            member.setUseYn("N");
            memberService.save(member);

            return ResponseEntity.ok()
                    .body(Map.of("message", "회원 탈퇴가 완료되었습니다."));

        } catch (Exception e) {
            log.error("회원 탈퇴 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원 탈퇴에 실패했습니다."));
        }
    }
}



