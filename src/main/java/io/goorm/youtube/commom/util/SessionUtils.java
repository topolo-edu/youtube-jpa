package io.goorm.youtube.commom.util;

import io.goorm.youtube.domain.Member;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    /**
     * 세션에서 Member 객체를 반환합니다.
     * 로그인하지 않은 경우 null을 반환합니다.
     */
    public static Member getMember(HttpSession session) {
        return (Member) session.getAttribute("member");
    }

    /**
     * 세션에서 Member 객체가 있을 경우 memberSeq를 반환합니다.
     * 로그인하지 않은 경우 null을 반환합니다.
     */
    public static Long getMemberSeq(HttpSession session) {
        Member member = getMember(session);
        return (member != null) ? member.getMemberSeq() : null;
    }
}
