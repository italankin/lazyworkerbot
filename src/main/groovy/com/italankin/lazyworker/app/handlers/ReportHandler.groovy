package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.utils.DateUtils
import io.fouad.jtb.core.builders.ApiBuilder

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class ReportHandler implements Handler {

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
    boolean handle(Command command) throws Exception {
        String rawArgs = command.getRawArgs()
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
                    return report(command, start, start + DAY_MILLIS)
                case "week":
                    Calendar calendar = DateUtils.getZoneCalendar(new Date())
                    long end = DateUtils.getStartOfDay(calendar.getTime()) + DAY_MILLIS
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    long start = DateUtils.getStartOfDay(calendar.getTime())
                    return report(command, start, end)
                case "month":
                    Calendar calendar = DateUtils.getZoneCalendar(new Date())
                    long end = DateUtils.getStartOfDay(calendar.getTime()) + DAY_MILLIS
                    calendar.set(Calendar.DATE, 1)
                    long start = DateUtils.getStartOfDay(calendar.getTime())
                    return report(command, start, end)
                default:
                    if (arg0.length() == 8) {
                        long start = DateUtils.getStartOfDay(DATE_FORMAT.parse(arg0))
                        return report(command, start, start + DAY_MILLIS)
                    } else {
                        try {
                            int offset = Integer.parseInt(arg0)
                            Calendar calendar = DateUtils.getZoneCalendar(new Date())
                            long end = DateUtils.getStartOfDay(calendar.getTime())
                            calendar.add(Calendar.DAY_OF_MONTH, offset)
                            long start = DateUtils.getStartOfDay(calendar.getTime())
                            return report(command, start, end)
                        } catch (NumberFormatException e) {
                            return false
                        }
                    }
            }
        } else {
            try {
                long start = DateUtils.getStartOfDay(DATE_FORMAT.parse(split[0]))
                long end = DateUtils.getStartOfDay(DATE_FORMAT.parse(split[1]))
                return report(command, start, end)
            } catch (ParseException e) {
                return false
            }
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /report start \\[end]"
    }

    private boolean report(Command command, long start, long end) {
        List<Activity> activities = activityManager.getActivitiesForInterval(command.getSenderId(), start, end)
        boolean oneDay = (end - start) <= DAY_MILLIS
        if (activities.isEmpty()) {
            StringBuilder sb = new StringBuilder("No activities for *")
            sb.append(DateUtils.day(start))
            if (!oneDay) {
                sb.append("* - *")
                sb.append(DateUtils.day(end - DAY_MILLIS))
            }
            sb.append("*.")
            return command.reply(sb.toString())
        }
        String name = "report_" + DateUtils.std(start)
        if (!oneDay) {
            name += "_" + DateUtils.std(end - DAY_MILLIS)
        }
        name += ".csv"
        StringBuilder sb = new StringBuilder()
        sb.append("Дата;Задача;Деятельность;Время в минутах")
        for (Activity activity : activities) {
            if (activity.isCurrent()) {
                continue
            }
            sb.append("\n")
            sb.append(DateUtils.std(activity.startTime))
            sb.append(";")
            sb.append(activity.name)
            sb.append(";")
            if (activity.comment) {
                String s = activity.comment
                        .replaceAll("#\\w+", "")
                        .replaceAll(" {2,}", " ")
                        .trim()
                sb.append(s)
            }
            sb.append(";")
            sb.append(DateUtils.minutes(activity.duration()))
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"))
        return ApiBuilder.api(command.getApi())
                .sendDocument(stream, name)
                .toChatId(command.getSenderId())
                .execute()
    }

}
