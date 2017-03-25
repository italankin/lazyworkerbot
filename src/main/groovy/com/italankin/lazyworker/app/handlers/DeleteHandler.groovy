package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler

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
    boolean handle(Command command) throws Exception {
        String rawArgs = command.getRawArgs()
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
            int userId = command.getSenderId()
            for (Integer id : ids) {
                Activity activity = activityManager.deleteActivity(userId, id)
                if (activity) {
                    command.reply("Deleted activity:\n${activity.detail()}")
                } else {
                    command.reply("No activity with id=`$id` found.")
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
                "*Use with care.*"
    }

}
