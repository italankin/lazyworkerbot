package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UpdateActivityHandler implements Handler {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateActivityHandler.class)

    private final ActivityManager activityManager

    UpdateActivityHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "update"
    }

    @Override
    boolean handle(Request request) throws Exception {
        String rawArgs = request.getRawArgs()
        if (!rawArgs || rawArgs.isEmpty()) {
            return false
        }
        String[] split = rawArgs.split("\\s+", 2)
        if (split.length < 2) {
            return false
        }
        int id
        try {
            id = Integer.parseInt(split[0])
        } catch (NumberFormatException e) {
            LOG.error("handle:", e)
            return false
        }

        int i = split[1].indexOf('\n')
        String name = i == -1 ? split[1] : split[1].substring(0, i).trim()
        if (name.isEmpty()) {
            return false
        }
        String comment = i == -1 ? null : split[1].substring(i).trim()
        Activity activity = activityManager.updateActivity(request.getSenderId(), id, name, comment)
        if (activity) {
            return request.response(activity.detail())
        } else {
            return request.response("No activity with id=`$id` found.")
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /update id newName \\[\\*comment]\n\n" +
                "_newName_ - new name of the activity\n" +
                "_comment_ - optional comment, *must* start on the new line"
    }

}
