package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.utils.DateUtils

abstract class AbstractFinishActivityHandler implements Handler {

    protected final ActivityManager activityManager

    AbstractFinishActivityHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    boolean finish(Command command) {
        long finishTime = DateUtils.currentTime()
        Activity activity = activityManager.finishCurrentActivity(command.getSenderId(), finishTime)
        if (activity) {
            String msg = String.format("Activity %s finished at _%s_.\nSession time: _%s_",
                    activity.desc(),
                    DateUtils.detail(finishTime),
                    DateUtils.pretty(activity.duration()))
            if (activity.comment != null && !activity.comment.isEmpty()) {
                msg = msg + "\n" + activity.comment
            }
            return command.reply(msg)
        } else {
            return false
        }
    }

}
