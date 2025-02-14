package io.goorm.youtube.controller;

import io.goorm.youtube.admin.MemberUpdateDTO;
import io.goorm.youtube.commom.util.PasswordUtil;
import io.goorm.youtube.commom.util.SessionUtils;
import io.goorm.youtube.domain.Member;
import io.goorm.youtube.service.impl.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberServiceImpl memberService;

    @Autowired
    public MemberController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }


    //회원가입
    @PostMapping("/join")
    public ResponseEntity<?>  join(@ModelAttribute Member member) {

        Map<String, Object> response = new HashMap<>();

        // 아이디 중복 검사
        if (memberService.existsById(member.getMemberId())) {

            response.put("message", "아이디가 중복 되었습니다.");
            return ResponseEntity.badRequest().body(response);

        }

        try {
            // 패스워드 암호화
            String encryptedPassword = PasswordUtil.encryptPassword(member.getMemberPw());
            member.setMemberPw(encryptedPassword);

            // 회원 저장
            memberService.save(member);

            URI location = URI.create("/api/join");
            return ResponseEntity.created(location).build();

        } catch (Exception e) {
            response.put("message", "회원가입에 실패하였습니다." + e.toString());
            return ResponseEntity.badRequest().body(response);
        }

    }


    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String memberId,
                                                     @RequestParam String memberPw,
                                                     HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        Member members = memberService.login(memberId);

        if ( members != null && validateLogin(members.getMemberPw(), memberPw) ) {

            log.debug("성공");
            session.setAttribute("member", members);

            response.put("message", "로그인에 성공했습니다.");
            return ResponseEntity.ok(response);

        } else {

            response.put("message", "로그인에 실패하였습니다.");
            return ResponseEntity.badRequest().body(response);

        }

    }

    public boolean validateLogin(String storedPassword, String password) {

        log.debug("storedPassword : " + storedPassword);
        log.debug("password : " + password);
        
        return storedPassword != null && PasswordUtil.matches(password, storedPassword);
    }


    //로그아웃
    @GetMapping("/logout")
    public  ResponseEntity<Map<String, Object>> logout(HttpSession session) {

        session.invalidate();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "로그아웃되었습니다.");

        return ResponseEntity.ok(response);
    }



    //profile수정
    @PutMapping("/members")
    public ResponseEntity<?>  update(@ModelAttribute MemberUpdateDTO memberUpdateDTO, HttpSession session) {

        Long memberSeqBySession = SessionUtils.getMemberSeq(session);

        if (memberSeqBySession == null) {

            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.badRequest().body(response);

        }

        if (memberSeqBySession != memberUpdateDTO.getMemberSeq()) {

            Map<String, Object> response = new HashMap<>();
            response.put("message", "본인의 정보가 아닙니다.");
            return ResponseEntity.badRequest().body(response);

        }

        memberService.update(memberSeqBySession, memberUpdateDTO);

        return ResponseEntity.ok(memberSeqBySession);

    }

}
