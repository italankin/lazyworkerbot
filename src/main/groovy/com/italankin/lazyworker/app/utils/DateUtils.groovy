package com.italankin.lazyworker.app.utils

import java.text.DateFormat
import java.text.SimpleDateFormat

final class DateUtils {

    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+5:00")
    public static final DateFormat DATE_FORMAT_DETAIL = new SimpleDateFormat("H:mm:ss 'on' d MMMM yyyy")
    public static final DateFormat DATE_FORMAT_DAY = new SimpleDateFormat("MMM d, EEE, yyyy")
    public static final DateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm")

    static {
        DATE_FORMAT_DETAIL.setTimeZone(TIME_ZONE)
        DATE_FORMAT_DAY.setTimeZone(TIME_ZONE)
        DATE_FORMAT_TIME.setTimeZone(TIME_ZONE)
    }

    static long currentTime() {
        return new Date().getTime()
    }

    static String day(long time) {
        return DATE_FORMAT_DAY.format(new Date(time))
    }

    static String detail(long time) {
        return DATE_FORMAT_DETAIL.format(new Date(time))
    }

    static String time(long time) {
        return DATE_FORMAT_TIME.format(new Date(time))
    }

    static String pretty(long time) {
        int s = (int) (time.intdiv(1000)) % 60
        int m = (int) ((time.intdiv(1000 * 60)) % 60)
        int h = (int) ((time.intdiv(1000L * 60 * 60)) % 24)
        return String.format("%dh %dm %ds", h, m, s)
    }

    private DateUtils() {
        throw new AssertionError()
    }
}
