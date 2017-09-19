package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Request

class AlterHandler extends CurrentHandler {

    AlterHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "alter"
    }

    @Override
    boolean handle(Request request) throws Exception {
        String rawArgs = request.getRawArgs()
        if (rawArgs == null || rawArgs.isEmpty()) {
            return request.response(helpMessage())
        }
        String[] args = rawArgs.split("\\s")
        if (args.length != 3) {
            return request.response(helpMessage())
        }
        int id, userId = request.getSenderId()
        long start, finish

        try {
            id = args[0].toInteger()
            start = args[1].toLong()
            finish = args[2].toLong()
        } catch (NumberFormatException e) {
            return request.response(helpMessage())
        }

        if (start < 0 || finish < 0) {
            return request.response("_start_ or _end_ must be greater than 0")
        }

        Activity activity = activityManager.getActivity(userId, id)
        if (!activity) {
            return request.response("No activity found.")
        }

        if (start == 0 && finish == 0) {
            return showActivity(request, activity)
        }

        long startTime = start == 0 ? activity.startTime : start
        long finishTime = finish == 0 ? activity.finishTime : finish

        if (finishTime != -1 && startTime >= finishTime) {
            return request.response("_start_=`$startTime` must be less than _end_=`$finishTime`")
        }

        activity = activityManager.updateActivity(userId, id, startTime, finishTime)

        return showActivity(request, activity)
    }

    @Override
    String helpMessage() {
        return "Usage: /alter id start end\n\n" +
                "_id_ - id of the activity\n" +
                "_start_ - new start time (unix epoch millis)\n" +
                "_end_ - new end time (unix epoch millis)\n\n" +
                "_start_ must be less than _end_. " +
                "If _start_ or _end_ set to `0`, that time will not be changed.\n"
    }
}
