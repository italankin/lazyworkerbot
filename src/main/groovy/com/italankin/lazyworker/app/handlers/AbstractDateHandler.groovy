package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.utils.DateUtils

abstract class AbstractDateHandler implements Handler {

    protected static final long ONE_DAY = 1000 * 60 * 60 * 24
    protected static final long INTERVAL_MAX = 100

    protected final ActivityManager activityManager

    AbstractDateHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    boolean handle(Command command) throws Exception {
        long[] interval
        try {
            interval = getInterval(command)
        } catch (Exception e) {
            return false
        }
        if (interval == null) {
            return false
        }
        long start = interval[0]
        long end = interval[1]
        if ((end - start) / ONE_DAY > INTERVAL_MAX) {
            return command.reply("The interval should not exceed 100 days.")
        }
        boolean oneDay = (end - start) <= ONE_DAY
        List<Activity> activities = activityManager.getActivitiesForInterval(command.getSenderId(), start, end)
        if (activities.isEmpty()) {
            StringBuilder sb = new StringBuilder("No activities for *")
            sb.append(DateUtils.day(start))
            if (!oneDay) {
                sb.append("* - *")
                sb.append(DateUtils.day(end))
            }
            sb.append("*.")
            return command.reply(sb.toString())
        }

        StringBuilder sb = new StringBuilder()
        sb.append("Acitvities for *")
        sb.append(DateUtils.day(start))
        if (!oneDay) {
            sb.append("* - *")
            sb.append(DateUtils.day(end))
        }
        sb.append("*:")
        long total = 0, subTotal = 0
        String c = null
        int s = activities.size()
        for (int i = 0; i < s; i++) {
            Activity activity = activities.get(i)
            long duration = activity.duration()
            total += duration
            if (!oneDay) {
                String d = DateUtils.day(activity.startTime)
                if (!c || c != d) {
                    if (subTotal > 0) {
                        sb.append("\nSubtotal: _")
                        sb.append(DateUtils.pretty(subTotal))
                        sb.append("_")
                    }
                    c = d
                    subTotal = 0
                    sb.append("\n_ --- ")
                    sb.append(c)
                    sb.append(" ---_")
                } else {
                    subTotal += duration
                }
            }
            sb.append("\n")
            sb.append("\\[")
            sb.append(DateUtils.time(activity.startTime))
            sb.append("] ")
            sb.append(activity.desc())
            sb.append(": _")
            sb.append(DateUtils.pretty(duration))
            sb.append("_")
        }
        sb.append("\n*Total*: _")
        sb.append(DateUtils.pretty(total))
        sb.append("_.")
        return command.reply(sb.toString())
    }

    protected abstract long[] getInterval(Command command)

}
