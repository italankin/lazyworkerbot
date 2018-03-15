package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request
import com.italankin.lazyworker.app.utils.DateUtils
import com.italankin.lazyworker.app.utils.StringUtils

class CurrentHandler implements Handler {

    protected final ActivityManager activityManager

    CurrentHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "current"
    }

    @Override
    boolean handle(Request request) throws Exception {
        Activity current = activityManager.getCurrentActivity(request.getSenderId())
        return showActivity(request, current)
    }

    protected static boolean showActivity(Request request, Activity current) {
        if (current == null) {
            return request.response("No activity found.")
        } else {
            String msg = String.format("Activity %s started at _%s_.\nSession time: _%s_",
                    current.desc(),
                    DateUtils.detail(current.startTime),
                    DateUtils.pretty(current.duration()))
            if (current.comment != null && !current.comment.isEmpty()) {
                msg = msg + "\n" + StringUtils.escapeMarkdown(current.comment)
            }
            return request.response(msg)
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /current"
    }

}
