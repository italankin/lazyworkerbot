package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.utils.DateUtils

class CurrentActivityHandler implements Handler {

    private final ActivityManager activityManager

    CurrentActivityHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "current"
    }

    @Override
    boolean handle(Command command) throws Exception {
        Activity current = activityManager.getCurrentActivity(command.getSenderId())
        if (current == null) {
            return command.reply("No current activity.")
        } else {
            String msg = String.format("Activity #%d *%s* started at _%s_.\nSession time: _%s_",
                    current.id,
                    current.name,
                    DateUtils.detail(current.startTime),
                    DateUtils.pretty(current.duration()))
            if (current.comment != null && !current.comment.isEmpty()) {
                msg = msg + "\n" + current.comment
            }
            return command.reply(msg)
        }
    }

    @Override
    String helpMessage() {
        return null
    }

}
