package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler

class LastActivitiesHandler implements Handler {

    private static final int LIMIT = 20

    private final ActivityManager activityManager

    LastActivitiesHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "last"
    }

    @Override
    boolean handle(Command command) throws Exception {
        List<Activity> activities = activityManager.getActivitiesByUserId(command.getSenderId(), LIMIT)
        if (activities.isEmpty()) {
            return command.reply("No activities found.")
        } else {
            StringBuilder sb = new StringBuilder()
            sb.append("Last activities:")
            int size = activities.size()
            for (int i = 0; i < size; i++) {
                Activity activity = activities.get(i)
                sb.append("\n")
                sb.append(i + 1)
                sb.append(". ")
                sb.append(activity.desc())
            }
            sb.append("\nTo see more inforation about specific activity use /show.")
            return command.reply(sb.toString())
        }
    }

    @Override
    String helpMessage() {
        return null
    }

}
