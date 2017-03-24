package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler

class UpdateActivityHandler implements Handler {

    private final ActivityManager activityManager

    UpdateActivityHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "update"
    }

    @Override
    boolean handle(Command command) throws Exception {
        String rawArgs = command.getRawArgs()
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
            return false
        }

        int i = split[1].indexOf('\n')
        String name = i == -1 ? split[1] : split[1].substring(0, i).trim()
        if (name.isEmpty()) {
            return false
        }
        String comment = i == -1 ? null : split[1].substring(i).trim()
        Activity activity = activityManager.updateActivity(command.getSenderId(), id, name, comment)
        if (activity) {
            return command.reply(activity.detail())
        } else {
            return command.reply("No activity found.")
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /update id new\\_name\n\\[comment]"
    }

}
