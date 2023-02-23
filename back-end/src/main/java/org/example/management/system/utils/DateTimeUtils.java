package org.example.management.system.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeUtils() {

    }


    public static String getTimeStr(LocalDateTime date) {
        return formatter.format(date);
    }

}
