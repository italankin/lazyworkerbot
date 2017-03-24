package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.utils.DateUtils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class DateHandler extends AbstractDateHandler {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd")

    static {
        DATE_FORMAT.setTimeZone(DateUtils.TIME_ZONE)
    }

    DateHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "date"
    }

    @Override
    protected long[] getInterval(Command command) throws Exception {
        String rawArgs = command.getRawArgs()
        if (rawArgs == null || rawArgs.isEmpty()) {
            return null
        }
        String[] args = rawArgs.split("\\s+", 2)
        switch (args.length) {
            case 1:
                String s = args[0]
                if (s.length() == 8) {
                    Date date = parse(s)
                    if (date != null) {
                        return intervalWithOffset(date, 1)
                    }
                } else if (s.length() in [1, 2, 3, 4]) {
                    try {
                        int offset = Integer.parseInt(s)
                        if (offset >= -100 && offset <= 0) {
                            Calendar calendar = DateUtils.getZoneCalendar(new Date())
                            calendar.add(Calendar.DAY_OF_MONTH, offset)
                            return intervalWithOffset(calendar.getTime(), 1)
                        }
                        return null
                    } catch (NumberFormatException e) {
                        return null
                    }
                }
                break
            case 2:
                Date start = parse(args[0])
                if (start == null) {
                    return null
                }
                Date end = parse(args[1])
                if (end == null) {
                    return null
                }
                return [getStartOfDay(start), getStartOfDay(end)]
        }
        return null
    }

    private static Date parse(String s) {
        try {
            return DATE_FORMAT.parse(s)
        } catch (ParseException e) {
            return null
        }
    }

    protected static long[] intervalWithOffset(Date from, int daysOffset) {
        long start = getStartOfDay(from)
        return [start, start + daysOffset * DAY_MILLIS]
    }

    protected static long getStartOfDay(Date from) {
        Calendar calendar = DateUtils.getZoneCalendar(from)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.getTimeInMillis()
    }


    @Override
    String helpMessage() {
        return "Usage: /date start \\[end]"
    }

}
