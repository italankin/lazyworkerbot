package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.activity.User
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request
import com.italankin.lazyworker.app.utils.DateUtils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class ReportHandler implements Handler {

    static final String PREF_REPORT_CSV_HEADER = "report.csv.header"

    static final String DEFAULT_CSV_HEADER = "Date;Name;Activity;Spent time (hrs)"

    protected static final long DAY_MILLIS = 1000 * 60 * 60 * 24

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd")

    private final ActivityManager activityManager

    ReportHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "report"
    }

    @Override
    boolean handle(Request request) throws Exception {
        String rawArgs = request.getRawArgs()
        if (!rawArgs || rawArgs.isEmpty()) {
            return false
        }
        String[] split = rawArgs.split("\\s+", 2)
        if (split.length == 0) {
            return false
        }
        if (split.length == 1) {
            String arg0 = split[0].trim()
            if (arg0.isEmpty()) {
                return false
            }
            switch (arg0) {
                case "today":
                    long start = DateUtils.getStartOfDay(new Date())
                    return report(request, start, start + DAY_MILLIS)
                case "week":
                    Calendar calendar = DateUtils.getZoneCalendar(new Date())
                    calendar.setFirstDayOfWeek(Calendar.MONDAY)
                    long end = DateUtils.getStartOfDay(calendar.getTime()) + DAY_MILLIS
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    long start = DateUtils.getStartOfDay(calendar.getTime())
                    return report(request, start, end)
                case "month":
                    Calendar calendar = DateUtils.getZoneCalendar(new Date())
                    long end = DateUtils.getStartOfDay(calendar.getTime()) + DAY_MILLIS
                    calendar.set(Calendar.DATE, 1)
                    long start = DateUtils.getStartOfDay(calendar.getTime())
                    return report(request, start, end)
                default:
                    if (arg0.length() == 8) {
                        long start = DateUtils.getStartOfDay(DATE_FORMAT.parse(arg0))
                        return report(request, start, start + DAY_MILLIS)
                    } else {
                        try {
                            int offset = Integer.parseInt(arg0)
                            Calendar calendar = DateUtils.getZoneCalendar(new Date())
                            long end = DateUtils.getStartOfDay(calendar.getTime())
                            calendar.add(Calendar.DAY_OF_MONTH, offset)
                            long start = DateUtils.getStartOfDay(calendar.getTime())
                            return report(request, start, end)
                        } catch (NumberFormatException e) {
                            return false
                        }
                    }
            }
        } else {
            try {
                long start = DateUtils.getStartOfDay(DATE_FORMAT.parse(split[0]))
                long end = DateUtils.getStartOfDay(DATE_FORMAT.parse(split[1]))
                return report(request, start, end)
            } catch (ParseException e) {
                return false
            }
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /report start \\[end]\n" +
                "Or: /report offset\n" +
                "Or: /report preset\n\n" +
                "_start_ - start date in format `yyyyMMdd` (inclusive)\n" +
                "_end_ - end date in format `yyyyMMdd` (exclusive); if not specified, defaults to next day after _start_\n" +
                "_offset_ - offset in days relative to current day\n" +
                "_preset_ - one of the following values: `today`, `week`, `month`"
    }

    private boolean report(Request request, long start, long end) {
        int userId = request.getSenderId()
        List<Activity> activities = activityManager.getActivitiesForIntervalSummary(userId, start, end)
        boolean oneDay = (end - start) <= DAY_MILLIS
        if (activities.isEmpty()) {
            StringBuilder sb = new StringBuilder("No activities for *")
            sb.append(DateUtils.day(start))
            if (!oneDay) {
                sb.append("* - *")
                sb.append(DateUtils.day(end - DAY_MILLIS))
            }
            sb.append("*.")
            return request.response(sb.toString())
        }
        String name = "report_" + DateUtils.std(start)
        if (!oneDay) {
            name += "_" + DateUtils.std(end - DAY_MILLIS)
        }
        name += ".csv"
        StringBuilder sb = new StringBuilder()
        User.Preference preference = activityManager.getUserPreference(userId, PREF_REPORT_CSV_HEADER)
        sb.append(preference?.value ?: DEFAULT_CSV_HEADER)
        for (Activity activity : activities) {
            if (activity.isCurrent()) {
                continue
            }
            sb.append("\n")
            sb.append(DateUtils.std(activity.startTime))
            sb.append(";")
            sb.append(activity.name())
            sb.append(";")
            if (activity.comment) {
                String s = activity.comment
                        .replaceAll("#\\w+", "")
                        .replaceAll(" {2,}", " ")
                        .trim()
                sb.append(s)
            }
            sb.append(";")
            long minutes
            if (activity.totalTime >= 0) {
                minutes = DateUtils.minutes(activity.totalTime)
            } else {
                minutes = DateUtils.minutes(activity.duration())
            }
            String time = BigDecimal.valueOf(minutes / 60d)
                    .setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString()
            sb.append(time)
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"))
        return request.response(stream, name)
    }

}
