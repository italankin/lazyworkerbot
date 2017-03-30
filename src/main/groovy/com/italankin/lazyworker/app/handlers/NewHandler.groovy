package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Request
import com.italankin.lazyworker.app.utils.DateUtils

class NewHandler extends AbstractFinishHandler {

    NewHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "new"
    }

    @Override
    boolean handle(Request request) throws Exception {
        int userId = request.getSenderId()
        String rawArgs = request.getRawArgs()
        if (rawArgs == null || rawArgs.isEmpty()) {
            rawArgs = "New activity"
        }
        String name
        String comment = null
        int commentStart = rawArgs.indexOf('\n')
        if (commentStart != -1) {
            name = rawArgs.substring(0, commentStart)
            comment = rawArgs.substring(commentStart).trim()
        } else {
            name = rawArgs
        }
        name = name.trim()
        if (name.length() > 50) {
            name = name.substring(0, 50)
        }
        if (finishCurrentActivity(request)) {
            // do not care
        }
        return startActivity(request, userId, name, comment)
    }

    protected boolean startActivity(Request request, int userId, String name, String comment) {
        Activity activity = activityManager.startActivity(userId, name, DateUtils.currentTime(), comment)
        if (activity) {
            return request.response("Activity ${activity.desc()} started.\nUse /finish to finish.")
        } else {
            return false
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /new \\[name] \\[\\*comment]\n\n" +
                "_name_ - name of the new activity (max: 50 characters)\n" +
                "_comment_ - optional comment, *must* start on the new line"
    }

}
