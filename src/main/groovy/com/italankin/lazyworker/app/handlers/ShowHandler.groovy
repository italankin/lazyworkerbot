package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler

class ShowHandler implements Handler {

    private final ActivityManager activityManager

    ShowHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "show"
    }

    @Override
    boolean handle(Command command) throws Exception {
        String rawArgs = command.getRawArgs()
        if (rawArgs == null || rawArgs.isEmpty()) {
            return false
        }
        int id
        try {
            id = Integer.parseInt(rawArgs)
        } catch (NumberFormatException e) {
            return false
        }
        Activity activity = activityManager.getActivity(command.getSenderId(), id)
        if (activity == null) {
            return command.reply("No activity found.")
        }
        return command.reply(activity.detail())
    }

    @Override
    String helpMessage() {
        return "Usage: /show id"
    }

}
