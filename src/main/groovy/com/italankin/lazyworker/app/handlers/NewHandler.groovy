package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.utils.DateUtils

class NewHandler extends AbstractFinishHandler {

    NewHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "new"
    }

    @Override
    boolean handle(Command command) throws Exception {
        int userId = command.getSenderId()
        String rawArgs = command.getRawArgs()
        if (rawArgs == null || rawArgs.isEmpty()) {
            rawArgs = "New activity"
        }
        String name
        String comment = null
        int commentStart = rawArgs.indexOf('\n')
        if (commentStart != -1) {
            name = rawArgs.substring(0, commentStart)
            comment = rawArgs.substring(commentStart).trim()
        } else {
            name = rawArgs
        }
        name = name.trim()
        if (name.length() > 50) {
            name = name.substring(0, 50)
        }
        if (finish(command)) {
            // do not care
        }
        Activity activity = activityManager.startActivity(userId, name, DateUtils.currentTime(), comment)
        if (activity != null) {
            return command.reply("Activity ${activity.desc()} started.\nUse /finish to finish.")
        } else {
            return false
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /new [name]\n\\[comment]"
    }

}
