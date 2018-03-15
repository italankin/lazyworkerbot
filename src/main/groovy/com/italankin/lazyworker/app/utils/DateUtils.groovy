package com.italankin.lazyworker.app.utils

import java.text.DateFormat
import java.text.SimpleDateFormat

final class DateUtils {

    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+5:00")
    public static final DateFormat DATE_FORMAT_DETAIL = new SimpleDateFormat("H:mm:ss 'on' d MMMM yyyy")
    public static final DateFormat DATE_FORMAT_DAY = new SimpleDateFormat("MMM d, EEE, yyyy")
    public static final DateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm")
    public static final DateFormat DATE_FORMAT_STD = new SimpleDateFormat("yyyy-MM-dd")

    static {
        DATE_FORMAT_DETAIL.setTimeZone(TIME_ZONE)
        DATE_FORMAT_DAY.setTimeZone(TIME_ZONE)
        DATE_FORMAT_TIME.setTimeZone(TIME_ZONE)
        DATE_FORMAT_STD.setTimeZone(TIME_ZONE)
    }

    static Calendar getZoneCalendar() {
        return getZoneCalendar(new Date())
    }

    static Calendar getZoneCalendar(Date from) {
        Calendar calendar = Calendar.getInstance(TIME_ZONE)
        calendar.setTime(from)
        return calendar
    }

    static long currentTime() {
        return new Date().getTime()
    }

    static long getStartOfDay(Date time) {
        Calendar calendar = getZoneCalendar(time)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.getTimeInMillis()
    }

    static String day(long time) {
        return DATE_FORMAT_DAY.format(new Date(time))
    }

    static String std(long time) {
        return DATE_FORMAT_STD.format(new Date(time))
    }

    static String detail(long time) {
        return DATE_FORMAT_DETAIL.format(new Date(time))
    }

    static String time(long time) {
        return DATE_FORMAT_TIME.format(new Date(time))
    }

    static String pretty(long time) {
        long s = seconds(time) % 60
        long m = minutes(time) % 60
        long h = hours(time)
        return String.format("%dh %dm %ds", h, m, s)
    }

    static long seconds(long time) {
        return time.intdiv(1000)
    }

    static long minutes(long time) {
        return time.intdiv(1000 * 60)
    }

    static long hours(long time) {
        return time.intdiv(1000 * 60 * 60)
    }

    private DateUtils() {
        throw new AssertionError()
    }
}
