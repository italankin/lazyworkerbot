package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.utils.DateUtils

class NewActivityHandler extends AbstractFinishActivityHandler {

    NewActivityHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "new"
    }

    @Override
    boolean handle(Command command) throws Exception {
        int userId = command.getSenderId()
        if (finish(command)) {
            // do not care
        }
        String rawArgs = command.getRawArgs()
        if (rawArgs == null || rawArgs.isEmpty()) {
            return false
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
        Activity activity = activityManager.startActivity(userId, name, DateUtils.currentTime(), comment)
        if (activity != null) {
            return command.reply("Activity #${activity.id} *$name* started and is current.\nUse /finish to finish current task.")
        } else {
            return false
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /new activity\\_name\n\\[comment]"
    }

}
