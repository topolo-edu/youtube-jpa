package io.goorm.youtube.controller;

import io.goorm.youtube.commom.util.PasswordUtil;
import io.goorm.youtube.domain.Member;
import io.goorm.youtube.service.impl.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


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
    public String  join(@ModelAttribute Member member) {


        // 아이디 중복 검사
        if (memberService.existsById(member.getMemberId())) {

            return "join"; // 중복인 경우 다시 회원가입 폼으로
        }

        try {
            // 패스워드 암호화
            String encryptedPassword = PasswordUtil.encryptPassword(member.getMemberPw());
            member.setMemberPw(encryptedPassword);

            // 회원 저장
            memberService.save(member);

            return "redirect:/login"; // 회원가입 성공시 로그인 페이지로 리다이렉트
        } catch (Exception e) {
            return "join"; // 예외 발생시 회원가입 폼으로
        }

    }


    //로그인
    @PostMapping("/login")
    public String login(@ModelAttribute Member member, HttpSession session) {


        Member members = memberService.login(member);

        log.debug(member.getMemberId());

        if ( members != null && validateLogin(members.getMemberPw(), member.getMemberPw()) ) {

            log.debug("성공");
            session.setAttribute("member", members);

            return "redirect:/";

        } else {

            return "login";

        }

    }

    public boolean validateLogin(String storedPassword, String password) {

        log.debug("storedPassword : " + storedPassword);
        log.debug("password : " + password);
        
        return storedPassword != null && PasswordUtil.matches(password, storedPassword);
    }


    //로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {

        session.invalidate();

        return "redirect:/";
    }



    //profile수정
    @PostMapping("/members/{memberSeq}")
    public String  update(@ModelAttribute Member member, RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("memberSeq", member.getMemberSeq());
        redirectAttributes.addFlashAttribute("msg", "수정에 성공하였습니다.");

        return "redirect:/members/{memberSeq}";

    }

}
