package io.goorm.youtube.commom.util;

public class StringUtils {

    /**
     * 문자열이 null이 아니고, 공백(trim) 후 길이가 0보다 크면 true를 반환합니다.
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
