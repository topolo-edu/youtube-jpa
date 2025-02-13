package io.goorm.youtube.commom.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component("dateFormatter")
public class DateFormatter {
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT));
    }

    public String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

    public String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}
