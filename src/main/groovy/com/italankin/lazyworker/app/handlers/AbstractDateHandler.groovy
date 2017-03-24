package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.utils.DateUtils

abstract class AbstractDateHandler implements Handler {

    protected static final long DAY_MILLIS = 1000 * 60 * 60 * 24
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
        if ((end - start) / DAY_MILLIS > INTERVAL_MAX) {
            return command.reply("The interval should not exceed $INTERVAL_MAX days.")
        }
        boolean oneDay = (end - start) <= DAY_MILLIS
        List<Activity> activities = activityManager.getActivitiesForInterval(command.getSenderId(), start, end)
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

        StringBuilder sb = new StringBuilder()
        sb.append("Acitvities for *")
        sb.append(DateUtils.day(start))
        if (!oneDay) {
            sb.append("* - *")
            sb.append(DateUtils.day(end))
        }
        sb.append("*:")
        if (oneDay) {
            print1(sb, activities)
        } else {
            printn(sb, activities)
        }
        return command.reply(sb.toString())
    }

    private static void print1(StringBuilder sb, List<Activity> activities) {
        int total = 0
        for (Activity activity : activities) {
            long duration = activity.duration()
            total += duration
            printActivity(sb, activity)
        }
        sb.append("\n*Total*: _")
        sb.append(DateUtils.pretty(total))
        sb.append("_.")
    }

    private static void printn(StringBuilder sb, List<Activity> activities) {
        String c = null
        long total = 0, subtotal = 0
        for (Activity activity : activities) {
            long duration = activity.duration()
            total += duration
            String d = DateUtils.day(activity.startTime)
            if (!c || c != d) {
                if (subtotal > 0) {
                    printSubtotal(sb, subtotal)
                }
                c = d
                subtotal = duration
                sb.append("\n_ --- ")
                sb.append(c)
                sb.append(" ---_")
            } else {
                subtotal += duration
            }
            printActivity(sb, activity)
        }
        if (subtotal > 0) {
            printSubtotal(sb, subtotal)
        }
        sb.append("\n*Total*: _")
        sb.append(DateUtils.pretty(total))
        sb.append("_.")
    }

    private static void printActivity(StringBuilder sb, Activity activity) {
        sb.append("\n")
        sb.append("\\[")
        sb.append(DateUtils.time(activity.startTime))
        sb.append("] ")
        if (activity.isCurrent()) {
            sb.append("+ ")
        }
        sb.append(activity.desc())
        sb.append(" - _")
        sb.append(DateUtils.pretty(activity.duration()))
        sb.append("_")
    }

    private static void printSubtotal(StringBuilder sb, long subtotal) {
        sb.append("\n - Subtotal: _")
        sb.append(DateUtils.pretty(subtotal))
        sb.append("_ -\n")
    }

    protected abstract long[] getInterval(Command command)

}
