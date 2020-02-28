package me.jigulsw.api.common;

import java.time.Instant;

public class DateUtil {

    public static long getNow() {
        return Instant.now().getEpochSecond();
    }

    public static long getNowMilli() {
        return Instant.now().toEpochMilli();
    }
}
