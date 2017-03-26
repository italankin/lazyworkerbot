package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Request
import com.italankin.lazyworker.app.utils.DateUtils

class WeekHandler extends DateHandler {

    WeekHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "week"
    }

    @Override
    protected long[] getInterval(Request request) {
        Calendar calendar = DateUtils.getZoneCalendar(new Date())
        calendar.setFirstDayOfWeek(Calendar.MONDAY)
        long end = DateUtils.getStartOfDay(calendar.getTime()) + DAY_MILLIS
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        long start = DateUtils.getStartOfDay(calendar.getTime())
        return [start, end]
    }

    @Override
    String helpMessage() {
        return "Usage: /week"
    }

}
