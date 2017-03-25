package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.utils.DateUtils

class MonthHandler extends DateHandler {

    MonthHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "month"
    }

    @Override
    protected long[] getInterval(Command command) {
        Calendar calendar = DateUtils.getZoneCalendar(new Date())
        long end = DateUtils.getStartOfDay(calendar.getTime()) + DAY_MILLIS
        calendar.set(Calendar.DATE, 1)
        long start = DateUtils.getStartOfDay(calendar.getTime())
        return [start, end]
    }

    @Override
    String helpMessage() {
        return "/month"
    }

}
