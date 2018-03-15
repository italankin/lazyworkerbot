package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request

class DeleteHandler implements Handler {

    private final ActivityManager activityManager

    DeleteHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "delete"
    }

    @Override
    boolean handle(Request request) throws Exception {
        String rawArgs = request.getRawArgs()
        if (rawArgs != null) {
            List<Integer> ids = []
            try {
                String[] split = rawArgs.split("\\s+")
                if (split.length == 0) {
                    return false
                }
                split.each {
                    ids += Integer.parseInt(it)
                }
            } catch (NumberFormatException e) {
                return false
            }
            int userId = request.getSenderId()
            for (Integer id : ids) {
                Activity activity = activityManager.deleteActivity(userId, id)
                if (activity) {
                    request.response("Deleted activity:\n${activity.detail()}")
                } else {
                    request.response("No activity with id=`$id` found.")
                }
            }
            return true
        } else {
            return false
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /delete ids\n\n" +
                "_ids_ - ids (separated by space) of the activities to delete\n\n" +
                "*Use with caution.*"
    }

}
