package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request
import com.italankin.lazyworker.app.utils.DateUtils
import com.italankin.lazyworker.app.utils.StringUtils

abstract class AbstractFinishHandler implements Handler {

    protected final ActivityManager activityManager

    AbstractFinishHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    boolean finishCurrentActivity(Request request) {
        long finishTime = DateUtils.currentTime()
        Activity activity = activityManager.finishCurrentActivity(request.getSenderId(), finishTime)
        if (activity) {
            String msg = String.format("Activity %s finished at _%s_.\nSession time: _%s_",
                    activity.desc(),
                    DateUtils.detail(finishTime),
                    DateUtils.pretty(activity.duration()))
            if (activity.comment != null && !activity.comment.isEmpty()) {
                msg = msg + "\n" + StringUtils.escapeMarkdown(activity.comment)
            }
            return request.response(msg)
        } else {
            return false
        }
    }

}
