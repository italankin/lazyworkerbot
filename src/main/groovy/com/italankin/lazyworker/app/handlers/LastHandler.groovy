package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request

class LastHandler implements Handler {

    private static final int LIMIT = 5

    private final ActivityManager activityManager

    LastHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "last"
    }

    @Override
    boolean handle(Request request) throws Exception {
        List<Activity> activities = activityManager.getActivitiesByUserId(request.getSenderId(), LIMIT)
        if (activities.isEmpty()) {
            return request.response("No activities found.")
        } else {
            StringBuilder sb = new StringBuilder()
            sb.append("Last activities:")
            int size = activities.size()
            for (int i = 0; i < size; i++) {
                Activity activity = activities.get(i)
                sb.append("\n")
                sb.append(i + 1)
                sb.append(". ")
                sb.append(activity.detail())
            }
            sb.append("\nTo see more inforation about specific activity use /show.")
            return request.response(sb.toString())
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /last"
    }

}
