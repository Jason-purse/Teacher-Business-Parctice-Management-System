package org.example.management.system.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private DateTimeUtils() {

    }

    public static long getStartTimeOfDay() {
        return getTimeOfDay(LocalDate.now().atStartOfDay());
    }

    public static long getTimeOfDay(LocalDateTime time) {
        return time.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }


    public static String getTimeStr(LocalDateTime date) {
        return formatter.format(date);
    }

}
