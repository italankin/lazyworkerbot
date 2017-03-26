package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request

class ShowHandler implements Handler {

    private final ActivityManager activityManager

    ShowHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "show"
    }

    @Override
    boolean handle(Request request) throws Exception {
        String rawArgs = request.getRawArgs()
        if (rawArgs == null || rawArgs.isEmpty()) {
            return false
        }
        int id
        try {
            id = Integer.parseInt(rawArgs)
        } catch (NumberFormatException e) {
            return false
        }
        Activity activity = activityManager.getActivity(request.getSenderId(), id)
        if (activity == null) {
            return request.response("No activity found.")
        }
        return request.response(activity.detail())
    }

    @Override
    String helpMessage() {
        return "Usage: /show id\n\n" +
                "_id_ - id of the activity to delete"
    }

}
