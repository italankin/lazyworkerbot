package com.italankin.lazyworker.app.handlers

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
            if (activityManager.deleteActivity(command.getSenderId(), id) > 0) {
                return command.reply("Deleted activity with id #$id.")
            } else {
                return command.reply("No activity found with id #$id.")
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
