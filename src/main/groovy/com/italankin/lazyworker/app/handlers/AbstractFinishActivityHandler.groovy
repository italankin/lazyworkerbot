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
        Activity current = activityManager.getCurrentActivity(command.getSenderId())
        if (current == null) {
            return false
        }
        long finishTime = DateUtils.currentTime()
        if (activityManager.finishCurrentActivity(command.getSenderId(), current.id, finishTime) > 0) {
            String msg = String.format("Activity %s finished at _%s_.\nSession time: _%s_",
                    current.desc(),
                    DateUtils.detail(finishTime),
                    DateUtils.pretty(finishTime - current.startTime))
            if (current.comment != null && !current.comment.isEmpty()) {
                msg = msg + "\n" + current.comment
            }
            return command.reply(msg)
        } else {
            return false
        }
    }

}
