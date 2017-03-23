package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler

class DeleteActivityHandler implements Handler {

    private final ActivityManager activityManager

    DeleteActivityHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "delete"
    }

    @Override
    boolean handle(Command command) throws Exception {
        String rawArgs = command.getRawArgs()
        if (rawArgs != null) {
            int id
            try {
                id = Integer.parseInt(rawArgs.trim())
            } catch (NumberFormatException e) {
                return false
            }
            Activity activity = activityManager.deleteActivity(command.getSenderId(), id)
            if (activity) {
                return command.reply("Deleted activity:\n${activity.detail()}.")
            } else {
                return command.reply("No activity found.")
            }
        } else {
            return false
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /delete activity\\_id"
    }

}
