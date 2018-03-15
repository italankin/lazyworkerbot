package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ResumeHandler extends NewHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NewHandler.class)

    ResumeHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "resume"
    }

    @Override
    boolean handle(Request request) throws Exception {
        int id = -1
        if (request.rawArgs) {
            try {
                id = request.rawArgs.toInteger()
            } catch (NumberFormatException e) {
                return false
            }
        }
        int userId = request.getSenderId()
        Activity current = activityManager.getCurrentActivity(userId)
        if (current && (id == -1 || current.id == id)) {
            return request.response("Activity ${current.desc()} is already running.")
        }
        Activity activity
        if (id != -1) {
            activity = activityManager.getActivity(userId, id)
            if (!activity) {
                return request.response("No activity with id=`$id` found.")
            }
        } else {
            activity = activityManager.getLatestFinishedActivity(userId)
            if (!activity) {
                return request.response("No activities found.")
            }
        }
        if (!finishCurrentActivity(null, request.getSenderId(), request.getApi())) {
            LOG.error("Current activity found, but failed to finish:\n$activity")
        }
        return startActivity(request, userId, activity.name, activity.comment)
    }

    @Override
    String helpMessage() {
        return "Usage: /resume \\[id]\n\n" +
                "_id_ - optional id to copy name and comment from"
    }

}
